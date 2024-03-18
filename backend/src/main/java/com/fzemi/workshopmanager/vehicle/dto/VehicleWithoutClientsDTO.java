package com.fzemi.workshopmanager.vehicle.dto;

import com.fzemi.workshopmanager.vehicle.config.FuelFormat;
import lombok.Builder;

import java.util.Date;

@Builder
public record VehicleWithoutClientsDTO(
        Long id,
        String vin,
        String manufacturer,
        String model,
        String licencePlate,
        Date productionDate,
        String color,
        Float engineCapacity,
        FuelFormat fuelType,
        Integer power
) {
}
