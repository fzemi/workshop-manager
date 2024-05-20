package com.fzemi.workshopmanager.client.exception;

import com.fzemi.workshopmanager.handler.AppException;
import com.fzemi.workshopmanager.handler.ErrorCodes;

public class ClientNotFoundException extends AppException {

    public ClientNotFoundException(String message) {
        super(ErrorCodes.CLIENT_NOT_FOUND, message);
    }
}
