package com.grepp.teamnotfound.infra.auth.oauth2.user;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class KakaoOAuth2UserInfo implements OAuth2UserInfo{

    private Map<String, Object> attributes;
    private Map<String, Object> attributesAccount;
    private Map<String, Object> attributesProfile;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.attributesAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.attributesProfile = (Map<String, Object>) attributesAccount.get("profile");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        // 카카오 email 제공 X 이슈로 id 값으로 식별
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
        return attributesProfile.get("nickname").toString();
    }
}