package com.lzk.auth.exception;

import com.lzk.common.exception.BusinessException;
import com.lzk.common.exception.IErrorCode;

public class AuthException extends BusinessException {
    public AuthException(IErrorCode message) {
        super(message);
    }
}
