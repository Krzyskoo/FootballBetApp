package com.example.demo.Dtos;

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
        description = "Reprezentuje pojedyncze wydarzenie sportowe dostępne do obstawiania"
)
public class InternalEventDTO {
    @Schema(
            description = "Unikalne ID wydarzenia zewnętrznego serwisu",
            example     = "evt_12345"
    )
    private String eventId;
    @Schema(
            description = "Klucz sportu (np. 'soccer', 'basketball')",
            example     = "soccer"
    )
    private String sportKey;
    @Schema(
            description = "Pełna nazwa sportu",
            example     = "Football"
    )
    private String sportTitle;
    @Schema(
            description = "Data i godzina rozpoczęcia wydarzenia (UTC)",
            type        = "string",
            format      = "date-time",
            example     = "2025-05-06T18:00:00Z"
    )
    private Date startTime;
    @Schema(
            description = "Nazwa drużyny gospodarzy",
            example     = "FC Barcelona"
    )
    private String homeTeam;
    @Schema(
            description = "Kurs na zwycięstwo drużyny gospodarzy",
            example     = "1.85"
    )
    private String homeTeamOdds;
    @Schema(
            description = "Nazwa drużyny gości",
            example     = "Real Madrid"
    )
    private String awayTeam;
    @Schema(
            description = "Kurs na zwycięstwo drużyny gości",
            example     = "2.10"
    )
    private String awayTeamOdds;
    @Schema(
            description = "Kurs na remis",
            example     = "3.50"
    )
    private String drawOdds;
    @Schema(
            description = "Aktualny status wydarzenia",
            implementation = Result.class
    )
    private Result status;
    @Schema(
            description = "Flaga informująca, czy wydarzenie jest zakończone",
            example     = "false"
    )
    private boolean completed;

}
