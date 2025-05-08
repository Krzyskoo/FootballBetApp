package com.example.demo.controller;

import com.example.demo.Dtos.InternalEventDTO;
import com.example.demo.mapper.EventMapper;
import com.example.demo.model.Event;
import com.example.demo.services.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;


    @GetMapping("/bet/events")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary     = "Pobierz listę wydarzeń",
            description = "Zwraca listę wszystkich nieukończonych wydarzeń dostępnych do obstawiania"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista wydarzeń zwrócona pomyślnie"),
            @ApiResponse(responseCode = "500", description = "Wewnętrzny błąd serwera")
    })
    public ResponseEntity<List<InternalEventDTO>> getEvents() {
        log.info("Getting events to place a bet");
        List<Event> events = eventService.getAllNonCompletedEvents();
        List<InternalEventDTO> dtos = eventMapper.toDtoList(events);
        return ResponseEntity.ok(dtos);

    }
}
