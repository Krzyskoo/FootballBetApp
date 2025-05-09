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
            summary     = "Get list of events",
            description = "Returns a list of all non-completed events available for betting."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "Events retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description  = "Internal server error"
            )
    })
    public ResponseEntity<List<InternalEventDTO>> getEvents() {
        log.info("Getting events to place a bet");
        List<Event> events = eventService.getAllNonCompletedEvents();
        List<InternalEventDTO> dtos = eventMapper.toDtoList(events);
        return ResponseEntity.ok(dtos);
    }
}
