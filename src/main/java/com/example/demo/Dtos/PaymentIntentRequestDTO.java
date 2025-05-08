package com.example.demo.Dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CreatePaymentIntentRequest", description = "Parametry inicjacji płatności Stripe")
public class PaymentIntentRequestDTO {
    @Schema(
            description = "Kwota do zapłaty w najniższej jednostce waluty (np. grosze/centy)",
            example = "5000",
            required = true
    )
    @Min(value = 1, message = "Amount musi być co najmniej 1")
    private Long amount;

    @Schema(
            description = "Waluta płatnosci USD)",
            example = "USD",
            required = true
    )
    @NotBlank(message = "Currency jest wymagane")
    private String currency;
}
