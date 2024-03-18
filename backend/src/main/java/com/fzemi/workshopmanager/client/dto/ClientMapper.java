package com.fzemi.workshopmanager.client.dto;

import com.fzemi.workshopmanager.client.entity.Client;
import com.fzemi.workshopmanager.vehicle.dto.VehicleWithoutClientsDTO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ClientMapper {
    // TODO: implement vehicles
    public Client toClient(ClientDTO clientDTO) {
        return Client.builder()
                .id(clientDTO.id())
                .firstname(clientDTO.firstname())
                .surname(clientDTO.surname())
                .pesel(clientDTO.pesel())
                .nip(clientDTO.nip())
                .phoneNumber(clientDTO.phoneNumber())
                .email(clientDTO.email())
                .country(clientDTO.country())
                .postalCode(clientDTO.postalCode())
                .city(clientDTO.city())
                .address(clientDTO.address())
                .birthDate(clientDTO.birthDate())
                .build();
    }

    public ClientDTO toClientDTO(Client client) {
        List<VehicleWithoutClientsDTO> vehicles = client.getVehicles() != null ?
                client.getVehicles().stream().map(vehicle -> VehicleWithoutClientsDTO.builder()
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
                        .build()).toList() : Collections.emptyList();

        return ClientDTO.builder()
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
                .vehicles(vehicles)
                .build();
    }

    public ClientWithoutVehiclesDTO toClientWithoutVehiclesDTO(Client client) {
        return ClientWithoutVehiclesDTO.builder()
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
                .build();
    }
}
