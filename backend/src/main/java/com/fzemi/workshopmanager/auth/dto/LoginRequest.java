package com.fzemi.workshopmanager.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record LoginRequest(
        @NotEmpty(message = "Username is required")
        @NotBlank(message = "Username is required")
        String username,
        @NotEmpty(message = "Password is required")
        @NotBlank(message = "Password is required")
        String password
) {
}
