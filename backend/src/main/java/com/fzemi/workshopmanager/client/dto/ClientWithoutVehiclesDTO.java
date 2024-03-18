package com.fzemi.workshopmanager.client.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record ClientWithoutVehiclesDTO(
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
        Date birthDate
) {
}
