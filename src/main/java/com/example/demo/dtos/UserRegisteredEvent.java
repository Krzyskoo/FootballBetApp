package com.example.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name        = "UserRegisteredEvent",
        description = "Event payload sent when a new user has successfully registered."
)
public class UserRegisteredEvent {
    @Schema(
            description = "Email address of the newly registered user",
            example     = "john.doe@example.com",
            required    = true
    )
    private String email;
    @Schema(
            description = "Unique identifier assigned to the user",
            example     = "42",
            required    = true
    )
    private Long userId;
}
