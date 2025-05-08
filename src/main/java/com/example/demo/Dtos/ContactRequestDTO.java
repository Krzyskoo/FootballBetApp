package com.example.demo.Dtos;

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
        description = "Payload zgłoszenia kontaktowego zawierający dane nadawcy i treść wiadomości"
)
public class ContactRequestDTO {
    @Schema(
            description = "Imię i nazwisko nadawcy wiadomości",
            example     = "Jan Kowalski",
            required    = true
    )
    private String name;
    @Schema(
            description = "Adres e-mail nadawcy, na który można wysłać odpowiedź",
            example     = "jan.kowalski@example.com",
            required    = true,
            format      = "email"
    )
    private String email;
    @Schema(
            description = "Temat wiadomości kontaktowej",
            example     = "Pytanie o ofertę VIP",
            required    = true
    )
    private String subject;
    @Schema(
            description = "Treść wiadomości kontaktowej",
            example     = "Dzień dobry, chciałbym uzyskać więcej informacji na temat waszej oferty VIP.",
            required    = true
    )
    private String message;


}
