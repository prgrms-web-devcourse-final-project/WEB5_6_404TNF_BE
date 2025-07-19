package com.grepp.teamnotfound.infra.auth.oauth2.user;

import java.util.Map;

public class NaverOAuth2UserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
        // json 내부의 response 내부의 value 값에 저장되어 있기 때문
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }
}
