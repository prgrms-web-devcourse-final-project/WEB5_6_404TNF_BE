package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReplyErrorCode implements BaseErrorCode{

    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "REPLY_001", "존재하지 않는 댓글입니다."),
    REPLY_REPORTED(HttpStatus.NOT_FOUND.value(), "REPLY_002", "관리자가 숨김 처리한 댓글입니다."),
    REPLY_DELETED(HttpStatus.NOT_FOUND.value(), "REPLY_003", "삭제된 댓글입니다."),
    REPLY_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "REPLY_004", "해당 댓글에 대한 접근 권한이 없습니다.");


    private final int status;
    private final String code;
    private final String message;
}
