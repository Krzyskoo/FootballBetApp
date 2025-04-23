package com.example.demo.controller;

import com.example.demo.Dtos.InternalEventDTO;
import com.example.demo.mapper.EventMapper;
import com.example.demo.model.Event;
import com.example.demo.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;


    @GetMapping("/bet/events")
    public ResponseEntity<List<InternalEventDTO>> getEvents() {
        List<Event> events = eventService.getAllNonCompletedEvents();
        List<InternalEventDTO> dtos = eventMapper.toDtoList(events);
        return ResponseEntity.ok(dtos);

    }
}
