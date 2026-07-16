package edu.valle.modules.users.dto.request;

import edu.valle.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank @Size(max = 120) String fullName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 60) String username,
        @Size(min = 6, max = 72) String password,
        @NotNull UserRole role,
        Boolean active
) {
}
