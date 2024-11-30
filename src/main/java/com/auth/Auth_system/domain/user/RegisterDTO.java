package com.auth.Auth_system.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
        @NotBlank(message = "Name is required.")
        @Size(min = 3)
        String name,
        @NotBlank(message = "Email is required.")
        String login,
        @NotBlank(message = "Password is required.")
        @Size(min = 8)
        String password,
        UserRole role) {
}
