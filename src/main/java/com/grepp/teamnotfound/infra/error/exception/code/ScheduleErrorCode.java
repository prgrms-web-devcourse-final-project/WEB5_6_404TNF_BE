package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ScheduleErrorCode implements BaseErrorCode{

    SCHEDULE_DATE_ERROR(HttpStatus.BAD_REQUEST.value(), "SCHE_001", "과거 날짜는 선택할 수 없습니다."),
    SCHEDULE_ALREADY_DONE(HttpStatus.BAD_REQUEST.value(), "SCHE_002", "이미 완료한 일정입니다."),
    SCHEDULE_CYCLE_EMPTY(HttpStatus.BAD_REQUEST.value(), "SCHE_003", "주기 종료일을 설정해야 합니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "SCHE_004", "존재하지 않는 일정입니다."),
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST.value(), "SCHE_005", "유효하지 않은 날짜입니다."),
    SCHEDULE_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "SCHE_006", "해당 일정에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;
}
