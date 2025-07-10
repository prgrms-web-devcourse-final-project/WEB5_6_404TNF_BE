package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WalkingErrorCode implements BaseErrorCode {

    WALKING_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "WALKING_001","존재하지 않는 산책입니다."),
    WALKING_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "WALKING_002", "해당 산책에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;

}
