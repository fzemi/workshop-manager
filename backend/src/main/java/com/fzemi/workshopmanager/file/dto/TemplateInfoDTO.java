package com.fzemi.workshopmanager.file.dto;

import lombok.Builder;

/**
 * DTO for document template information.
 */
@Builder
public record TemplateInfoDTO(
        String name,
        String displayName,
        String description
) {
}
