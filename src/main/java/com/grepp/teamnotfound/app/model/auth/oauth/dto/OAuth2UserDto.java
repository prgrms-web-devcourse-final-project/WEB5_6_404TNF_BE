package com.grepp.teamnotfound.app.model.auth.oauth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OAuth2UserDto {

    private String name;
    private String email;
    private Long userId;
    private String role;
}
