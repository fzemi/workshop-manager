package com.fzemi.workshopmanager.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCodes {
    // 10xx - SECURITY
    BAD_CREDENTIALS(1000, UNAUTHORIZED, "Bad credentials"),
    ACCOUNT_LOCKED(1001, UNAUTHORIZED, "Account locked"),
    // 11xx - REPAIRS ERRORS
    REPAIR_NOT_FOUND(1100, NOT_FOUND, "Repair not found"),
    // 12xx - VEHICLES ERRORS
    VEHICLE_NOT_FOUND(1200, NOT_FOUND, "Vehicle not found"),
    // 13xx - CLIENTS ERRORS
    CLIENT_NOT_FOUND(1300, NOT_FOUND, "Client not found"),
    // 14xx - PARTS ERRORS
    PART_NOT_FOUND(1400, NOT_FOUND, "Part not found"),
    REPAIR_PART_NOT_FOUND(1401, NOT_FOUND, "Repair part not found"),
    // 15xx - FILES ERRORS
    FILE_NOT_UPLOADED(1500, INTERNAL_SERVER_ERROR, "File not uploaded"),
    FILE_NOT_FOUND(1501, NOT_FOUND, "File not found"),
    FILE_ALREADY_EXISTS(1502, CONFLICT, "File already exists"),
    NO_FILE_ACCESS(1503, FORBIDDEN, "No access to file"),
    // OTHER
    NO_CODE(0, NOT_IMPLEMENTED, "No code");

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    ErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }
}
