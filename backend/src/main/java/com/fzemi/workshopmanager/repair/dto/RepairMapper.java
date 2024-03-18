package com.fzemi.workshopmanager.repair.dto;

import com.fzemi.workshopmanager.client.dto.ClientMapper;
import com.fzemi.workshopmanager.client.dto.ClientWithoutVehiclesDTO;
import com.fzemi.workshopmanager.repair.entity.Repair;
import com.fzemi.workshopmanager.vehicle.dto.VehicleMapper;
import com.fzemi.workshopmanager.vehicle.dto.VehicleWithoutClientsDTO;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RepairMapper {

    private final VehicleMapper vehicleMapper;
    private final ClientMapper clientMapper;

    @Autowired
    public RepairMapper(VehicleMapper vehicleMapper,
                        ClientMapper clientMapper) {
        this.vehicleMapper = vehicleMapper;
        this.clientMapper = clientMapper;
    }

    public RepairDTO toRepairDTO(Repair repair) {
        VehicleWithoutClientsDTO vehicle = repair.getVehicle() != null ?
                vehicleMapper.toVehicleWithoutClientsDTO(repair.getVehicle()) : null;

        return RepairDTO.builder()
                .id(repair.getId())
                .number(repair.getNumber())
                .startDate(repair.getStartDate())
                .expectedEndDate(repair.getExpectedEndDate())
                .type(repair.getType())
                .vehicle(vehicle)
                .build();
    }

    public RepairWithClientsDTO toRepairWithClientsDTO(Repair repair) {
        VehicleWithoutClientsDTO vehicle = repair.getVehicle() != null ?
                vehicleMapper.toVehicleWithoutClientsDTO(repair.getVehicle()) : null;

        List<ClientWithoutVehiclesDTO> clients = vehicle != null ?
                repair.getClients().stream().map(clientMapper::toClientWithoutVehiclesDTO).toList() : Collections.emptyList();

        return RepairWithClientsDTO.builder()
                .id(repair.getId())
                .number(repair.getNumber())
                .startDate(repair.getStartDate())
                .expectedEndDate(repair.getExpectedEndDate())
                .type(repair.getType())
                .vehicle(vehicle)
                .clients(clients)
                .build();
    }
}
