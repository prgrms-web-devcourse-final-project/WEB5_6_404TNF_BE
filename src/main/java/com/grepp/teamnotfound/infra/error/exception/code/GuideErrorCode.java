package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GuideErrorCode implements BaseErrorCode {

    GUIDE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "GUIDE_001", "존재하지 않는 가이드입니다.");

    private final int status;
    private final String code;
    private final String message;
}
