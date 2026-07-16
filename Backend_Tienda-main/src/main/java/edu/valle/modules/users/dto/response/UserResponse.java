package edu.valle.modules.users.dto.response;

import edu.valle.common.enums.UserRole;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String username,
        UserRole role,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}