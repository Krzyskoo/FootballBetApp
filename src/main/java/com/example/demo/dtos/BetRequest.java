package com.example.demo.dtos;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name        = "BetRequest",
        description = "Data required to place a bet: amount and list of selections."
)
public class BetRequest {
    @Schema(
            description = "Total amount to stake in the account currency (e.g., 50.00)",
            example     = "50.00",
            required    = true
    )
    private BigDecimal amount;
    @ArraySchema(
            schema      = @Schema
                    (implementation = BetSelectionRequest.class,
                    description = "List of individual selections within the bet (e.g., match result, over/under, etc.)",
                    required    = true),
            minItems    = 1
    )
    private List<BetSelectionRequest> selections;

}
