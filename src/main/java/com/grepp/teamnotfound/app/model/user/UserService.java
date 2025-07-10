package com.grepp.teamnotfound.app.model.user;

import com.grepp.teamnotfound.app.model.auth.code.Role;
import com.grepp.teamnotfound.app.model.auth.mail.MailService;
import com.grepp.teamnotfound.app.model.user.dto.RegisterRequestDto;
import com.grepp.teamnotfound.app.model.user.dto.UserDto;
import com.grepp.teamnotfound.app.model.user.dto.UserImgDto;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.entity.UserImg;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.error.exception.AuthException;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.UserErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    ModelMapper modelMapper = new ModelMapper();

    // 요청
    @Transactional
    public void requestRegisterVerification(RegisterRequestDto requestDto) {
        // 1. 이메일 중복 확인 - 이메일이 존재하고, 이미 인증된 경우에만 예외
        // 이메일이 존재하고, 인증되지 않은 경우에는 새로 입력된 Dto로 업데이트

        //1-1. 이메일 중복 여부 확인
        Optional<User> existingUserOp = userRepository.findByEmail(requestDto.getEmail());

        User user;

        if(existingUserOp.isPresent()) {
            User existingUser = existingUserOp.get();
            // 이메일 인증까지 된 유저 : 진짜 중복
            if(existingUser.getVerifiedEmail()){
                throw new BusinessException(UserErrorCode.USER_EMAIL_ALREADY_EXISTS);
            } else{
                // 이메일 인증은 안 된 유저 : 업데이트 대상
                existingUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
                existingUser.setName(requestDto.getName());
                existingUser.setNickname(requestDto.getNickname());

                user = existingUser;
            }
        } else {
            // 완전히 새로운 사용자
            user = User.builder()
                    .email(requestDto.getEmail())
                    .name(requestDto.getName()) // 이름 필드 추가
                    .nickname(requestDto.getNickname())
                    .password(passwordEncoder.encode(requestDto.getPassword()))
                    .role(Role.ROLE_USER) // 기본 역할 설정
                    .provider("local") // 로컬 가입
                    .verifiedEmail(false) // 이메일 미인증 상태로 저장
                    .build();
        }

        // 2. 닉네임 중복 확인 (위에서 build해서, Dto의 nick은 중복 X)
        Optional<User> existingNicknameOp = userRepository.findByNickname(requestDto.getNickname());
        if(existingNicknameOp.isPresent() && !existingNicknameOp.get().getEmail().equals(requestDto.getEmail())){
            throw new BusinessException(UserErrorCode.USER_NICKNAME_ALREADY_EXISTS);
        }

        userRepository.save(user);

        // 4. 인증 이메일 발송 및 코드 Redis 저장
        mailService.sendVerificationEmail(requestDto.getEmail());

    }

    // 인증 코드 검증 및 최종 회원가입
    @Transactional
    public Long completeRegistration(String email, String verificationCode) {
        // 1. 인증 코드 검증
        mailService.verifyEmailCode(email, verificationCode);

        // 2. 사용자 인증 완료
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(UserErrorCode.USER_NOT_FOUND));

        if (user.getVerifiedEmail()) {
            throw new AuthException(UserErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        user.setVerifiedEmail(true);
        userRepository.save(user);

        return user.getUserId();
    }

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
}
