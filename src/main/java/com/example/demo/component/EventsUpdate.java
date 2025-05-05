package com.example.demo.component;

import com.example.demo.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component()
@RequiredArgsConstructor
public class EventsUpdate {

    private final EventService eventService;

    //@Scheduled(fixedRate = 36000000)
    public void addNewEventsDaily() {
        eventService.getSportsKeysFromNonCompletedEvents().stream()
                .forEach(eventService::getEventsForSports);
    }

}
