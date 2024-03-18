package com.fzemi.workshopmanager.client.dto;

import com.fzemi.workshopmanager.vehicle.dto.VehicleWithoutClientsDTO;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record ClientDTO(
        Long id,
        String firstname,
        String surname,
        String pesel,
        String nip,
        String phoneNumber,
        String email,
        String country,
        String postalCode,
        String city,
        String address,
        Date birthDate,
        List<VehicleWithoutClientsDTO> vehicles
) {
}
