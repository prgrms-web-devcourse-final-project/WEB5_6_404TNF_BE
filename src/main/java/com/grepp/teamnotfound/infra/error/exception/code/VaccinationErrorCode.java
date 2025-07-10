package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VaccinationErrorCode implements BaseErrorCode{

    VACCINATION_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "VAC_001", "존재하지 않는 예방접종입니다."),
    VACCINATION_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "VAC_002", "존재하지 않는 예방접종의 유형입니다."),
    VACCINATION_DATE_IN_FUTURE(HttpStatus.BAD_REQUEST.value(), "VAC_003", "접종 기록은 미래 일자 선택이 불가능합니다."),
    VACCINE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "VAC_001", "존재하지 않는 백신입니다.");

    private final int status;
    private final String code;
    private final String message;
}
