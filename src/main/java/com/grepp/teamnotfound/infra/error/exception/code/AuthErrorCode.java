package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode{

    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED.value(), "AUTH_001", "인증에 실패했습니다."),
    UNAUTHORIZED(HttpStatus.FORBIDDEN.value(), "AUTH_002", "접근 권한이 없습니다."),

    // jwt
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "AUTH_003", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "AUTH_004", "만료된 토큰입니다."),

    // OAuth
    OAUTH_PROVIDER_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "AUTH_005", "외부 인증 서버와의 호출에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
