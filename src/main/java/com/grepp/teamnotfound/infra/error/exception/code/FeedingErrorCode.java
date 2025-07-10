package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FeedingErrorCode implements BaseErrorCode {

    Feeding_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Feeding_001","존재하지 않는 식사입니다."),
    Feeding_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "Feeding_002", "해당 식사에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;

}