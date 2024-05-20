package com.fzemi.workshopmanager.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_IMPLEMENTED;

@Getter
public enum ErrorCodes {
    // 10xx - SECURITY
    // 11xx - REPAIRS ERRORS
    REPAIR_NOT_FOUND(1100, NOT_FOUND, "Repair not found"),
    // 12xx - VEHICLES ERRORS
    VEHICLE_NOT_FOUND(1200, NOT_FOUND, "Vehicle not found"),
    // 13xx - CLIENTS ERRORS
    CLIENT_NOT_FOUND(1300, NOT_FOUND, "Client not found"),
    // 14xx - PARTS ERRORS
    PART_NOT_FOUND(1400, NOT_FOUND, "Part not found"),
    REPAIR_PART_NOT_FOUND(1401, NOT_FOUND, "Repair part not found"),
    // OTHER
    NO_CODE(0, NOT_IMPLEMENTED, "No code");

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
