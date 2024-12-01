package com.auth.Auth_system.domain.user;

public record RegisterDTO(String name, String login, String password, UserRole role) {
}
