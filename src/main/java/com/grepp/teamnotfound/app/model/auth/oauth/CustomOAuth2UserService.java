package com.grepp.teamnotfound.app.model.auth.oauth;

import com.grepp.teamnotfound.app.model.auth.code.Role;
import com.grepp.teamnotfound.app.model.auth.oauth.dto.CustomOAuth2UserDto;
import com.grepp.teamnotfound.app.model.auth.oauth.dto.OAuth2UserDto;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.infra.auth.oauth2.user.GoogleOAuth2UserInfo;
import com.grepp.teamnotfound.infra.auth.oauth2.user.KakaoOAuth2UserInfo;
import com.grepp.teamnotfound.infra.auth.oauth2.user.NaverOAuth2UserInfo;
import com.grepp.teamnotfound.infra.auth.oauth2.user.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // 리소스가 제공하는 유저 정보
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("1️⃣ 리소스가 제공하는 유저 정보 - oAuth2User: {}", oAuth2User);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;

        // 각 provider별 Info 객체 제작
        if (registrationId.equals("naver")) {
            oAuth2UserInfo = new NaverOAuth2UserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());
        }else if(registrationId.equals("kakao")){
            oAuth2UserInfo = new KakaoOAuth2UserInfo(oAuth2User.getAttributes());

        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth2 provider : " + userRequest.getClientRegistration().getProviderDetails());
        }

        String userEmail = oAuth2UserInfo.getEmail();

        Optional<User> optionalUser = userRepository.findByEmail(userEmail);
        User user;
        if (optionalUser.isEmpty()) {
            log.info("2️⃣-1️⃣ 신규 생성되는 유저 email - oAuth2UserInfo.getEmail: {}", oAuth2UserInfo.getEmail());

            String tempNickname = generateUniqueNickname();

            user = User.builder()
                    .email(oAuth2UserInfo.getEmail())
                    .name(oAuth2UserInfo.getName())
                    .nickname(tempNickname)
                    .role(Role.ROLE_USER)
                    .provider(oAuth2UserInfo.getProvider())
                    .build();

            userRepository.save(user);

            // 인증 객체 생성을 위한 dto
            OAuth2UserDto userDto = OAuth2UserDto.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .userId(user.getUserId())
                    .role("ROLE_USER")
                    .build();

            return new CustomOAuth2UserDto(userDto);

        } else if (Objects.equals(optionalUser.get().getProvider(), oAuth2UserInfo.getProvider())) {
            log.info("2️⃣-2️⃣ 기존에 존재하는 동일 공급자 email - oAuth2UserInfo.getEmail: {}", oAuth2UserInfo.getEmail());
            user = optionalUser.get();

            // 인증 객체 생성을 위한 dto
            OAuth2UserDto userDto = OAuth2UserDto.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .userId(user.getUserId())
                    .role("ROLE_USER")
                    .build();

            return new CustomOAuth2UserDto(userDto);

        } else {
            log.info("2️⃣-3️⃣ 기존에 존재하는 다른 공급자 email - oAuth2UserInfo.getEmail: {}", oAuth2UserInfo.getEmail());
            OAuth2Error error = new OAuth2Error("INVALID_PROVIDER", "다른 provider로 가입된 이메일", null);
            throw new OAuth2AuthenticationException(error, "다른 provider로 가입된 이메일: " + oAuth2UserInfo.getEmail());
        }
    }

    private String generateUniqueNickname() {
        String nickname;
        do {
            nickname = generateNickname();
        } while (userRepository.existsByNickname(nickname));
        return nickname;
    }

    private String generateNickname() {

        String[] adjs = {"용감한", "귀여운", "활발한", "똑똑한", "총명한", "느긋한", "멋있는"};
        String[] dogs = {"푸들", "말티즈", "비숑", "포메라니안", "진돗개", "치와와", "웰시코기",
                "요크셔테리어", "시바견", "리트리버", "보더콜리", "불독", "시츄", "닥스훈트",
                "허스키", "도베르만", "슈나우저", "댕댕이"};

        Random random = new Random();

        String adj = adjs[random.nextInt(adjs.length)];
        String dog = dogs[random.nextInt(dogs.length)];
        int num = random.nextInt(10000);

        return adj+dog+num;
    }
}