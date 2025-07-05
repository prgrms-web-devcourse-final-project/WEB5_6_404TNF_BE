package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum StructuredDataErrorCode implements BaseErrorCode{

    LIFERECORD_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "LIFE_001", "존재하지 않는 기록입니다."),
    LIFERECORD_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "LIFE_002", "기록에 대한 접근 권한이 없습니다."),
    LIFERECORD_DATE_IN_FUTURE(HttpStatus.BAD_REQUEST.value(), "LIFE_003", "미래 기록은 작성이 불가능합니다.");

    private final int status;
    private final String code;
    private final String message;
}
