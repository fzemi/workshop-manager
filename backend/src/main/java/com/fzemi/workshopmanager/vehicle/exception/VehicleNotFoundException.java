package com.fzemi.workshopmanager.vehicle.exception;

import com.fzemi.workshopmanager.handler.AppException;
import com.fzemi.workshopmanager.handler.ErrorCodes;

public class VehicleNotFoundException extends AppException {
    public VehicleNotFoundException(String message) {
        super(ErrorCodes.VEHICLE_NOT_FOUND, message);
    }
}
