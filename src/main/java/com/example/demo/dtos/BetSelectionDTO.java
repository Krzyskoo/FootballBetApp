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
        name        = "BetSelectionDTO",
        description = "Represents a single selection within a bet, including the event, odds, and outcome."
)
public class BetSelectionDTO {
    @Schema(
            description = "Unique identifier of the bet selection",
            example     = "1"
    )
    private Long id;
    @Schema(
            description     = "Details of the event for this selection",
            implementation = InternalEventDTO.class
    )
    private InternalEventDTO event;
    @Schema(
            description = "Odds locked in at the time the bet was placed",
            example     = "1.85"
    )
    private BigDecimal lockedOdds;
    @Schema(
            description = "Predicted outcome for this selection",
            example     = "HOME_WIN"
    )
    private String predictedResult;
    @Schema(
            description = "Indicates whether this selection resulted in a win",
            example     = "false"
    )
    private boolean isWon;
    @Schema(
            description = "Indicates whether the event for this selection has completed",
            example     = "false"
    )
    private boolean completed;
}
