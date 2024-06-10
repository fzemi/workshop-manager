package com.fzemi.workshopmanager.file.exception;

import com.fzemi.workshopmanager.handler.AppException;
import com.fzemi.workshopmanager.handler.ErrorCodes;

public class FileUploadException extends AppException {
    public FileUploadException(String error, ErrorCodes errorCode) {
        super(errorCode, error);
    }

    public FileUploadException(String error, ErrorCodes errorCode, Throwable cause) {
        super(errorCode, error, cause);
    }
}
