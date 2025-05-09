package com.example.demo.dtos;

import com.example.demo.model.Result;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(
        name        = "InternalEventDTO",
        description = "Represents a single sports event available for betting."
)
public class InternalEventDTO {
    @Schema(
            description = "Unique identifier of the event from the external service",
            example     = "evt_12345"
    )
    private String eventId;
    @Schema(
            description = "Sport key (e.g., 'soccer', 'basketball')",
            example     = "soccer"
    )
    private String sportKey;
    @Schema(
            description = "Full name of the sport",
            example     = "Football"
    )
    private String sportTitle;
    @Schema(
            description = "Start date and time of the event (UTC)",
            type        = "string",
            format      = "date-time",
            example     = "2025-05-06T18:00:00Z"
    )
    private Date startTime;
    @Schema(
            description = "Name of the home team",
            example     = "FC Barcelona"
    )
    private String homeTeam;
    @Schema(
            description = "Odds for the home team to win",
            example     = "1.85"
    )
    private String homeTeamOdds;
    @Schema(
            description = "Nazwa drużyny gości",
            example     = "Real Madrid"
    )
    private String awayTeam;
    @Schema(
            description = "Odds for the away team to win",
            example     = "2.10"
    )
    private String awayTeamOdds;
    @Schema(
            description = "Odds for a draw",
            example     = "3.50"
    )
    private String drawOdds;
    @Schema(
            description = "Odds for a draw",
            example     = "3.50"
    )
    private Result status;
    @Schema(
            description = "Flag indicating whether the event is completed",
            example     = "false"
    )
    private boolean completed;

}
