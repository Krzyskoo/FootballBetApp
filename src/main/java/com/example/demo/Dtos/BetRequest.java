package com.example.demo.Dtos;

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
        description = "Dane potrzebne do złożenia zakładu: kwota i lista wyborów"
)
public class BetRequest {
    @Schema(
            description = "Całkowita kwota zakładu w walucie konta (np. 100.00)",
            example     = "50.00",
            required    = true
    )
    private BigDecimal amount;
    @ArraySchema(
            schema      = @Schema
                    (implementation = BetSelectionRequest.class,
                    description = "Lista poszczególnych wyborów w ramach zakładu (np. wynik meczu, over/under itp.)",
                    required    = true),
            minItems    = 1
    )
    private List<BetSelectionRequest> selections;

}
