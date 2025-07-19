package com.grepp.teamnotfound.app.model.auth.oauth.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2UserDto implements OAuth2User {

    private final OAuth2UserDto userDto;

    // 구글과 네이버의 반환이 달라서 사용 x
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // role 값 return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userDto.getRole();
            }
        });

        return collection;    }

    @Override
    public String getName() {

        return userDto.getName();
    }

    // 구분값 email
    public String getUsername() {

        return userDto.getEmail();
    }

    public Long getUserId() {

        return userDto.getUserId();
    }
}
