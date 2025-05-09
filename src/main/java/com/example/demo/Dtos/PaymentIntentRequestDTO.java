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
@Schema(
        name        = "CreatePaymentIntentRequest",
        description = "Parameters for initializing a Stripe payment."
)
public class PaymentIntentRequestDTO {
    @Schema(
            description = "Amount to be paid in the smallest currency unit (e.g., cents)",
            example     = "5000",
            required    = true
    )
    @Min(value = 1, message = "Amount musi być co najmniej 1")
    private Long amount;

    @Schema(
            description = "Payment currency (e.g., USD)",
            example     = "USD",
            required    = true
    )
    @NotBlank(message = "Currency jest wymagane")
    private String currency;
}
