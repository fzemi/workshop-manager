package com.fzemi.workshopmanager.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCodes {
    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    REPAIR_NOT_FOUND(404, NOT_FOUND, "Repair not found"),
    ;

    // TODO: Add codes for auth

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    ErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
