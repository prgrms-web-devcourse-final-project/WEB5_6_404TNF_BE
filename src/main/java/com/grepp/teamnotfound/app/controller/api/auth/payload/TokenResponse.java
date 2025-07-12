package com.grepp.teamnotfound.app.controller.api.auth.payload;

import com.grepp.teamnotfound.infra.auth.token.code.GrantType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private GrantType grantType;
    private Long expiresIn;


}
