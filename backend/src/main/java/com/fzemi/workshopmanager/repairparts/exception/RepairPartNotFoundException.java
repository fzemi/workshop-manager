package com.fzemi.workshopmanager.repairparts.exception;

import com.fzemi.workshopmanager.handler.AppException;
import com.fzemi.workshopmanager.handler.ErrorCodes;

public class RepairPartNotFoundException extends AppException {
    public RepairPartNotFoundException(String error) {
        super(ErrorCodes.REPAIR_PART_NOT_FOUND, error);
    }
}
