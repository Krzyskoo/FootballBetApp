package com.example.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@AllArgsConstructor
@Getter
@Setter
@Schema(
        name        = "CustomerDTO",
        description = "Represents the profile of the currently authenticated customer."
)
public class CustomerDTO {
    @Schema(
            description = "Customer's email address",
            example     = "john.doe@example.com"
    )
    private String email;
    @Schema(
            description = "Current account balance of the customer",
            example     = "1500.00"
    )
    private BigDecimal balance;

}
