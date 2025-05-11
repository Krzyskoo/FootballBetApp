package com.example.demo.dtos;

import com.example.demo.model.Result;
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
        name        = "BetSelectionRequest",
        description = "Data for a single selection within a bet request."
)
public class BetSelectionRequest {
    @Schema(
            description = "Unique identifier of the event from the external service",
            example     = "evt_12345",
            required    = true
    )
    private String eventId;
    @Schema(
            description     = "Predicted outcome for this selection",
            example         = "HOME_WIN",
            required        = true,
            implementation  = Result.class
    )
    private Result predictedResult;

}
