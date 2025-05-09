package com.example.demo.controller;

import com.example.demo.dtos.InternalEventDTO;
import com.example.demo.mapper.EventMapper;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.services.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
public class EventControllerTest {
    private MockMvc mockMvc;
    private EventService eventService;
    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        eventMapper  = mock(EventMapper.class);

        EventController controller = new EventController(eventService, eventMapper);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("GET /bet/events -> 200 + pusty JSON array")
    void getEvents_empty_thenReturnsEmptyJsonArray() throws Exception {
        // given
        when(eventService.getAllNonCompletedEvents()).thenReturn(List.of());
        when(eventMapper .toDtoList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/bet/events")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(eventService).getAllNonCompletedEvents();
        verify(eventMapper ).toDtoList(List.of());
    }

    @Test
    @DisplayName("GET /bet/events -> 200 + dwie pozycje w JSON")
    void getEvents_twoItems_thenReturnsJsonArray() throws Exception {
        Event e1 = new Event(); e1.setEventId("evt1");
        Event e2 = new Event(); e2.setEventId("evt2");
        List<Event> events = List.of(e1, e2);

        InternalEventDTO d1 = new InternalEventDTO(
                "evt1","Soccer","soccer",
                new Date(), "TeamA","1.5","TeamB","2.5","3.0",
                Result.PENDING, false
        );
        InternalEventDTO d2 = new InternalEventDTO(
                "evt2","Soccer","soccer",
                new Date(), "P1","2.0","P2","3.0","4.0",
                Result.PENDING, false
        );
        List<InternalEventDTO> dtos = List.of(d1, d2);

        when(eventService.getAllNonCompletedEvents()).thenReturn(events);
        when(eventMapper.toDtoList(events)).thenReturn(dtos);

        mockMvc.perform(get("/bet/events")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].eventId", is("evt1")))
                .andExpect(jsonPath("$[0].sportTitle", is("soccer")))
                .andExpect(jsonPath("$[0].homeTeam", is("TeamA")))
                .andExpect(jsonPath("$[1].eventId", is("evt2")))
                .andExpect(jsonPath("$[1].sportKey", is("Soccer")))
                .andExpect(jsonPath("$[1].awayTeam", is("P2")));

        verify(eventService).getAllNonCompletedEvents();
        verify(eventMapper).toDtoList(events);
    }
}
