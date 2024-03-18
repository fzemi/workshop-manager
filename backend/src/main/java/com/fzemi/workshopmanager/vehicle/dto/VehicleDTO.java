package com.fzemi.workshopmanager.vehicle.dto;

import com.fzemi.workshopmanager.client.dto.ClientWithoutVehiclesDTO;
import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.vehicle.config.FuelFormat;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record VehicleDTO(
        Long id,
        String vin,
        String manufacturer,
        String model,
        String licencePlate,
        Date productionDate,
        String color,
        Float engineCapacity,
        FuelFormat fuelType,
        Integer power,
        List<ClientWithoutVehiclesDTO> clients
) {
}
