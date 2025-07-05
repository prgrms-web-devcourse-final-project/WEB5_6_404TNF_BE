package com.grepp.teamnotfound.infra.error.exception;

import com.grepp.teamnotfound.infra.error.exception.code.BaseErrorCode;

public class AuthException extends CommonException{

    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }

}
