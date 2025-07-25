package com.grepp.teamnotfound.infra.error.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode{

    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "USER_001", "존재하지 않는 유저입니다."),
    USER_LOGIN_FAILED(HttpStatus.UNAUTHORIZED.value(), "USER_002", "아이디 또는 비밀번호가 일치하지 않습니다."),
    USER_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "USER_003", "이미 존재하는 이메일입니다."),
    USER_NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT.value(),"USER_004", "이미 존재하는 닉네임입니다."),
    USER_ACCOUNT_LOCKED(HttpStatus.FORBIDDEN.value(), "USER_005", "접근이 불가능한 계정입니다."),
    EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST.value(), "USER_006", "이메일 인증에 실패하였습니다."),
    EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST.value(), "USER_009", "이메일 인증이 이미 완료된 사용자입니다."),
    EMAIL_VERIFICATION_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "USER_008", "이메일 전송에 실패했습니다."),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN.value(), "USER_007", "해당 사용자 정보에 대한 접근 권한이 없습니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST.value(), "USER_008", "비밀번호는 영문/숫자/특수문자를 포함한 8~20자여야 합니다."),
    USER_REPORTED(HttpStatus.FORBIDDEN.value(), "USER_009", "신고로 인해 게시글/댓글 작성이 제한되었습니다.");

    private final int status;
    private final String code;
    private final String message;
}
