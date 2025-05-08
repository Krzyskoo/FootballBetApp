package com.example.demo.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RegisterUserRequest", description = "Dane potrzebne do rejestracji nowego użytkownika")
public class RegisterRequestDTO {
    @Schema(
            description = "Adres e-mail użytkownika",
            example = "jan.kowalski@example.com",
            required = true
    )
    @Email(message = "Musi być poprawny email")
    @NotBlank(message = "Email jest wymagany")
    private String email;

    @Schema(
            description = "Hasło użytkownika (min. 8 znaków)",
            example = "P@ssw0rd!",
            required = true
    )
    @NotBlank(message = "Hasło jest wymagane")
    private String password;
}
