package com.example.demo.controller;

import com.example.demo.model.Event;
import com.example.demo.services.EventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    @GetMapping("/bet/{sport}")
    public List<Event> getEvents(@PathVariable String sport) {

        return eventService.getEventsForSports(sport);
    }
}
