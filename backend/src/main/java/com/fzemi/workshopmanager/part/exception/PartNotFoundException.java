package com.fzemi.workshopmanager.part.exception;

import com.fzemi.workshopmanager.handler.AppException;
import com.fzemi.workshopmanager.handler.ErrorCodes;

public class PartNotFoundException extends AppException {

    public PartNotFoundException(String message) {
        super(ErrorCodes.PART_NOT_FOUND, message);
    }
}
