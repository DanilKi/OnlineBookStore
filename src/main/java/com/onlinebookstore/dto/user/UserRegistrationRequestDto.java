package com.onlinebookstore.dto.user;

import com.onlinebookstore.validation.FieldMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@FieldMatch(firstField = "password", secondField = "repeatPassword",
        message = "Both password fields must match")
public class UserRegistrationRequestDto {
    @NotEmpty @Email
    private String email;
    @NotNull @Size(min = 8, max = 32)
    private String password;
    @NotNull @Size(min = 8, max = 32)
    private String repeatPassword;
    @NotBlank @Size(max = 45)
    private String firstName;
    @NotBlank @Size(max = 45)
    private String lastName;
    private String shippingAddress;
}
