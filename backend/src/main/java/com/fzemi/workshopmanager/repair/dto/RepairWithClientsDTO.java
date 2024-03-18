package com.fzemi.workshopmanager.repair.dto;

import com.fzemi.workshopmanager.client.dto.ClientWithoutVehiclesDTO;
import com.fzemi.workshopmanager.repair.config.RepairTypeFormat;
import com.fzemi.workshopmanager.vehicle.dto.VehicleWithoutClientsDTO;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record RepairWithClientsDTO(
        Long id,
        String number,
        Date startDate,
        Date expectedEndDate,
        RepairTypeFormat type,
        VehicleWithoutClientsDTO vehicle,
        List<ClientWithoutVehiclesDTO> clients
) {
}
