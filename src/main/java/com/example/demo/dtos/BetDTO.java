package com.example.demo.dtos;

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
        description = "Represents a placed bet along with its details."
)
public class BetDTO {
    @Schema(description = "Unique identifier of the bet", example = "42")
    private Long betId;
    @Schema(description = "Łączne kursy mnożące stake", example = "3.75")
    private BigDecimal totalOdds;
    @Schema(description = "Combined odds multiplier applied to the stake", example = "100.00")
    private BigDecimal stake;
    @Schema(
            description = "Current status of the bet. Automatically assigned upon creation and updated during processing",
            example     = "OPEN",
            allowableValues = {"WON", "LOST", "Pending" }
    )
    private String status;
    @Schema(description = "Potential winning amount (stake × totalOdds)", example = "375.00")
    private BigDecimal winAmount;
    @ArraySchema(
            schema      = @Schema(implementation = BetSelectionDTO.class,
                    description = "Details of each selection included in the bet")

    )
    private List<BetSelectionDTO> selections;
    @Schema(
            description = "Timestamp when the bet was created",
            example     = "2025-05-01T14:30:00Z",
            type        = "string",
            format      = "date-time"
    )
    private Date createdDt;

}
