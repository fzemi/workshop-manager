package com.fzemi.workshopmanager.vehicle.dto;

import com.fzemi.workshopmanager.client.dto.ClientWithoutVehiclesDTO;
import com.fzemi.workshopmanager.vehicle.entity.Vehicle;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class VehicleMapper {
    public Vehicle toVehicle(VehicleDTO vehicleDTO) {
        // TODO: implement clients
        return Vehicle.builder()
                .id(vehicleDTO.id())
                .vin(vehicleDTO.vin())
                .manufacturer(vehicleDTO.manufacturer())
                .model(vehicleDTO.model())
                .licencePlate(vehicleDTO.licencePlate())
                .productionDate(vehicleDTO.productionDate())
                .color(vehicleDTO.color())
                .engineCapacity(vehicleDTO.engineCapacity())
                .fuelType(vehicleDTO.fuelType())
                .power(vehicleDTO.power())
                .build();
    }

    public VehicleDTO toVehicleDTO(Vehicle vehicle) {
        List<ClientWithoutVehiclesDTO> clients = vehicle.getClients() != null ?
                vehicle.getClients().stream().map(client -> ClientWithoutVehiclesDTO.builder()
                        .id(client.getId())
                        .firstname(client.getFirstname())
                        .surname(client.getSurname())
                        .pesel(client.getPesel())
                        .nip(client.getNip())
                        .phoneNumber(client.getPhoneNumber())
                        .email(client.getEmail())
                        .country(client.getCountry())
                        .postalCode(client.getPostalCode())
                        .city(client.getCity())
                        .address(client.getAddress())
                        .birthDate(client.getBirthDate())
                        .build()).toList() : Collections.emptyList();

        return VehicleDTO.builder()
                .id(vehicle.getId())
                .vin(vehicle.getVin())
                .manufacturer(vehicle.getManufacturer())
                .model(vehicle.getModel())
                .licencePlate(vehicle.getLicencePlate())
                .productionDate(vehicle.getProductionDate())
                .color(vehicle.getColor())
                .engineCapacity(vehicle.getEngineCapacity())
                .fuelType(vehicle.getFuelType())
                .power(vehicle.getPower())
                .clients(clients)
                .build();
    }

    public VehicleWithoutClientsDTO toVehicleWithoutClientsDTO(Vehicle vehicle) {
        return VehicleWithoutClientsDTO.builder()
                .id(vehicle.getId())
                .vin(vehicle.getVin())
                .manufacturer(vehicle.getManufacturer())
                .model(vehicle.getModel())
                .licencePlate(vehicle.getLicencePlate())
                .productionDate(vehicle.getProductionDate())
                .color(vehicle.getColor())
                .engineCapacity(vehicle.getEngineCapacity())
                .fuelType(vehicle.getFuelType())
                .power(vehicle.getPower())
                .build();
    }
}
