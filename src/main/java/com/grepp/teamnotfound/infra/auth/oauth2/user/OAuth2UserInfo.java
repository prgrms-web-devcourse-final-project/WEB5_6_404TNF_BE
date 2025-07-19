package com.grepp.teamnotfound.infra.auth.oauth2.user;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2UserInfo {


    // 제공자가 발급하는 id
    String getProviderId();
    // 제공자(naver, google, ...) -> User Entity의 provider
    String getProvider();
    // 사용자 실명 -> User Entity의 name
    String getName();
    // 사용자 이메일 -> User Entity의 email
    String getEmail();

}
