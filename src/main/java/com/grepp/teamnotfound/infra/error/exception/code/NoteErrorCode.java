package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NoteErrorCode implements BaseErrorCode{

    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "NOTE_001","존재하지 않는 관찰노트입니다."),
    NOTE_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "NOTE_002", "해당 관찰노트에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;
}
