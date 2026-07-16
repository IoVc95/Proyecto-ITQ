package edu.valle.modules.auth.dto.response;

public record AuthResponse(
        String token,
        String type,
        Long userId,
        String fullName,
        String username,
        String role
) {
}
