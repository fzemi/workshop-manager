package com.fzemi.workshopmanager.client.dto;

import lombok.Builder;

import java.util.Date;

/**
 * DTO for Client entity without vehicles.
 * Resolves infinite recursion in JSON serialization.
 */
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
