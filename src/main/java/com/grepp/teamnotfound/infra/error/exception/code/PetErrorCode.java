package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PetErrorCode implements BaseErrorCode{

    PET_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "PET_001", "존재하지 않는 반려견 정보입니다."),
    PET_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "PET_002", "이미 등록된 반려견입니다."),
    PET_INVALID_DATES(HttpStatus.BAD_REQUEST.value(), "PET_003", "반려견을 만난날은 생일 이전일 수 없습니다."),
    PET_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "PET_004", "해당 반려견에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;
}
