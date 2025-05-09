package com.example.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name        = "ContactRequestDTO",
        description = "Contact request payload containing sender’s details and message content."
)
public class ContactRequestDTO {
    @Schema(
            description = "Sender’s full name",
            example     = "John Doe",
            required    = true
    )
    private String name;
    @Schema(
            description = "Sender’s email address to receive a response",
            example     = "john.doe@example.com",
            required    = true,
            format      = "email"
    )
    private String email;
    @Schema(
            description = "Subject of the contact message",
            example     = "Inquiry about VIP package",
            required    = true
    )
    private String subject;
    @Schema(
            description = "Content of the contact message",
            example     = "Hello, I would like more information about your VIP package.",
            required    = true
    )
    private String message;


}
