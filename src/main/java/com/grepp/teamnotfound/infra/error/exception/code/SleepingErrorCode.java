package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SleepingErrorCode implements BaseErrorCode {

    SLEEPING_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "SLEEPING_001","존재하지 않는 수면시간입니다."),
    SLEEPING_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "SLEEPING_002", "해당 수면시간에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;

}
