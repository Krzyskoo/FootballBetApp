package com.example.demo.Dtos;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Schema(
        name        = "BetDTO",
        description = "Reprezentuje złożony zakład wraz z jego szczegółami"
)
public class BetDTO {
    @Schema(description = "Unikalne ID zakładu", example = "42")
    private Long betId;
    @Schema(description = "Łączne kursy mnożące stake", example = "3.75")
    private BigDecimal totalOdds;
    @Schema(description = "Postawiona kwota (stake)", example = "100.00")
    private BigDecimal stake;
    @Schema(
            description = "Status zakładu, status jest automatycznie przydziany po utworzeniu i w trakcie przetwarzania zakładu",
            example     = "OPEN",
            allowableValues = {"WON", "LOST", "Pending" }
    )
    private String status;
    @Schema(description = "Potencjalna wygrana (stake × totalOdds)", example = "375.00")
    private BigDecimal winAmount;
    @ArraySchema(
            schema      = @Schema(implementation = BetSelectionDTO.class,
                    description = "Szczegóły poszczególnych wyborów w zakładzie")

    )
    private List<BetSelectionDTO> selections;
    @Schema(
            description = "Data utworzenia zakładu",
            example     = "2025-05-01T14:30:00Z",
            type        = "string",
            format      = "date-time"
    )
    private Date createdDt;

}
