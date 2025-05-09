package com.example.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(
        name        = "RegisterUserRequest",
        description = "Data required to register a new user."
)
public class RegisterRequestDTO {
    @Schema(
            description = "User's email address",
            example     = "john.doe@example.com",
            required    = true
    )
    @Email(message = "Musi być poprawny email")
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @Schema(
            description = "User's password (min. 8 characters)",
            example     = "P@ssw0rd!",
            required    = true
    )
    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
}
