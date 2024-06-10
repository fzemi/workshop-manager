package com.fzemi.workshopmanager.handler;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCodes errorCode;

    public AppException(ErrorCodes errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public AppException(ErrorCodes errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCodes errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
