package com.fzemi.workshopmanager.part.dto;

import lombok.Builder;

@Builder
public record PartDTO(
        Long id,
        String partName,
        String serialNumber,
        String manufacturer
) {
}
