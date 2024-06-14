package com.onlinebookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank @Email @Size(max = 45)
        String email,
        @NotNull @Size(min = 8, max = 32)
        String password
) {
}
