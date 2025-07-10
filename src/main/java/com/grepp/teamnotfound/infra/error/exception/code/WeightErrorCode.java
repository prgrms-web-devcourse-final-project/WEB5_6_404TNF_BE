package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WeightErrorCode implements BaseErrorCode {

    WEIGHT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "WEIGHT_001","존재하지 않는 몸무게입니다."),
    WEIGHT_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "WEIGHT_002", "해당 몸무게에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;

}