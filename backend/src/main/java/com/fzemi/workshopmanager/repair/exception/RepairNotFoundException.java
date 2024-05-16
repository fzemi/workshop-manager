package com.fzemi.workshopmanager.repair.exception;

import com.fzemi.workshopmanager.handler.AppException;
import com.fzemi.workshopmanager.handler.ErrorCodes;

public class RepairNotFoundException extends AppException {
    public RepairNotFoundException(String error) {
        super(ErrorCodes.REPAIR_NOT_FOUND, error);
    }
}
