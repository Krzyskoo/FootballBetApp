package com.example.demo.controller;

import com.example.demo.Dtos.BetDTO;
import com.example.demo.Dtos.BetRequest;
import com.example.demo.mapper.BetMapper;
import com.example.demo.model.Bet;
import com.example.demo.services.BetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BetController {

    private final BetService betService;
    private final BetMapper betMapper;

    @PostMapping("/bets/place")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary     = "Złóż nowy zakład",
            description = "Umożliwia użytkownikowi postawienie zakładu na wybrany mecz"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description  = "Zakład został pomyślnie utworzony",
                    content      = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(implementation = Bet.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description  = "Nieprawidłowe dane wejściowe",
                    content      = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "500",
                    description  = "Wewnętrzny błąd serwera",
                    content      = @Content(schema = @Schema())
            )
    })
    public ResponseEntity<BetDTO> placeBet(@Validated @RequestBody BetRequest betRequest) {
        Bet createdBet = betService.createBet(betRequest);
        BetDTO createdBetDTO = betMapper.toBetDTO(createdBet);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBetDTO);
    }
    @GetMapping("/bets")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary     = "Pobierz zakłady użytkownika",
            description = "Zwraca listę wszystkich zakładów złożonych aktualnie zalogowanym użytkownikiem"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Lista zakładów użytkownika",
                    content      = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array       = @ArraySchema(schema = @Schema(implementation = BetDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description  = "Brak autoryzacji",
                    content      = @Content(schema = @Schema())
            )
    })
    public ResponseEntity<List<BetDTO>> getBetsCreatedByUser(){
        return ResponseEntity.status(HttpStatus.OK).body(betService.getBetsCreatedByUser());
    }

}
