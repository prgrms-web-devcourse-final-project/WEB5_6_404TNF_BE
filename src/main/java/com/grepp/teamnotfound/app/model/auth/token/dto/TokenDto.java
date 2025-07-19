package com.grepp.teamnotfound.app.model.auth.token.dto;

import com.grepp.teamnotfound.infra.auth.token.JwtProvider;
import com.grepp.teamnotfound.infra.auth.token.code.GrantType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private GrantType grantType;
    private Long atExpiresIn;
    private Long rtExpiresIn;

    public static TokenDto of(String accessToken, String refreshToken, JwtProvider jwtProvider) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(GrantType.BEARER)
                .atExpiresIn(jwtProvider.getAtExpiration())
                .rtExpiresIn(jwtProvider.getRtExpiration())
                .build();
    }

}