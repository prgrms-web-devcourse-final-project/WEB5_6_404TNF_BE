package com.grepp.teamnotfound.app.model.user;

import com.grepp.teamnotfound.app.model.auth.code.Role;
import com.grepp.teamnotfound.infra.util.mail.MailService;
import com.grepp.teamnotfound.app.model.user.dto.RegisterCommand;
import com.grepp.teamnotfound.app.model.user.dto.UserDto;
import com.grepp.teamnotfound.app.model.user.dto.UserImgDto;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.entity.UserImg;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    ModelMapper modelMapper = new ModelMapper();

    public UserDto findByUserId(Long userId) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        UserDto userDto = modelMapper.map(user, UserDto.class);

        UserImg userImg = user.getUserImg();
        if (userImg != null) {
            UserImgDto userImgDto = new UserImgDto();
            userImgDto.setUserImgId(userImg.getUserImgId());
            userImgDto.setUrl(userImg.getSavePath() + userImg.getRenamedName());

            userDto.setUserImg(userImgDto);
        }

        return userDto;
    }

    @Transactional
    public Long registerAdmin(RegisterCommand request) {

        validateEmailDuplication(request.getEmail());
        validateNicknameDuplication(request.getNickname());

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_ADMIN)
                .provider("local")
                .build();

        userRepository.save(user);
        return user.getUserId();
    }


    @Transactional
    public Long registerUser(RegisterCommand request) {

        validateEmailDuplication(request.getEmail());
        validateNicknameDuplication(request.getNickname());

        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .provider("local")
                .build();

        userRepository.save(user);
        return user.getUserId();
    }

    public void sendEmail(String email) {
        mailService.sendVerificationEmail(email);
    }

    public void validateEmailDuplication(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new BusinessException(UserErrorCode.USER_EMAIL_ALREADY_EXISTS);
        });
    }

    public void validateNicknameDuplication(String nickname) {
        userRepository.findByNickname(nickname).ifPresent(user -> {
            throw new BusinessException(UserErrorCode.USER_NICKNAME_ALREADY_EXISTS);
        });
    }
}
