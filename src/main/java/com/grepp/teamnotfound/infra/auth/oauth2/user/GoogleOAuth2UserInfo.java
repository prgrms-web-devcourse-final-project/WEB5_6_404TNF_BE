package com.grepp.teamnotfound.infra.auth.oauth2.user;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleOAuth2UserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }

    @Override
    public String getProvider() {
        return "google";
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
