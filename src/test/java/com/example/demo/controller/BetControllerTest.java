package com.example.demo.controller;

import com.example.demo.Dtos.BetDTO;
import com.example.demo.Dtos.BetRequest;
import com.example.demo.Dtos.BetSelectionDTO;
import com.example.demo.Dtos.InternalEventDTO;
import com.example.demo.mapper.BetMapper;
import com.example.demo.model.Bet;
import com.example.demo.model.BetSelection;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.services.BetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
public class BetControllerTest {
    private MockMvc mockMvc;
    private BetService betService;
    private BetMapper betMapper;

    @BeforeEach
    void setUp() {
        betService = mock(BetService.class);
        betMapper = mock(BetMapper.class);
        BetController controller = new BetController(betService,betMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("POST /bets/place → 201 + Bet z selekcjami")
    void placeBet_withSelections_thenReturnsCreatedWithSelections() throws Exception {
        Event ev1 = new Event(); ev1.setEventId("evt1");
        Event ev2 = new Event(); ev2.setEventId("evt2");
        InternalEventDTO eventDTO = new InternalEventDTO(); eventDTO.setEventId("evt1");
        InternalEventDTO eventDTO2 = new InternalEventDTO(); eventDTO2.setEventId("evt2");


        BetSelection sel1 = BetSelection.builder()
                .id(1L)
                .event(ev1)
                .lockedOdds(new BigDecimal("1.90"))
                .predictedResult(Result.HOME_WIN)
                .completed(false)
                .build();
        BetSelection sel2 = BetSelection.builder()
                .id(2L)
                .event(ev2)
                .lockedOdds(new BigDecimal("2.10"))
                .predictedResult(Result.DRAW)
                .completed(false)
                .build();

        Bet created = Bet.builder()
                .betId(123L)
                .stake(new BigDecimal("10.00"))
                .totalOdds(new BigDecimal("3.50"))
                .status("PENDING")
                .winAmount(new BigDecimal("35.00"))
                .selections(List.of(sel1, sel2))
                .build();
        BetDTO betDto = new BetDTO(
                123L,
                new BigDecimal("3.50"),
                new BigDecimal("10.00"),
                "PENDING",
                new BigDecimal("35.00"),
                List.of(
                        new BetSelectionDTO(sel1.getId(),eventDTO,sel1.getLockedOdds(),sel1.getPredictedResult().name(),sel1.isWon(),sel1.isCompleted()),
                        new BetSelectionDTO(sel2.getId(),eventDTO2,sel2.getLockedOdds(),sel2.getPredictedResult().name(),sel2.isWon(),sel2.isCompleted())
                ),
                new Date()
        );

// 2) Zstubuj mapper
        when(betMapper.toBetDTO(created)).thenReturn(betDto);


        when(betService.createBet(any(BetRequest.class))).thenReturn(created);
        when(betMapper.toBetDTO(created)).thenReturn(betDto);


        String jsonRequest = """
            {
              "stake": 10.00,
              "totalOdds": 3.50,
              "selections": [
                {"eventId":"evt1","lockedOdds":1.90,"predictedResult":"HOME_WIN"},
                {"eventId":"evt2","lockedOdds":2.10,"predictedResult":"DRAW"}
              ]
            }
            """;

        mockMvc.perform(post("/bets/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.stake", is(10.00)))
                .andExpect(jsonPath("$.totalOdds", is(3.50)))
                .andExpect(jsonPath("$.winAmount", is(35.00)))

                .andExpect(jsonPath("$.selections", hasSize(2)))

                .andExpect(jsonPath("$.selections[0].event.eventId", is("evt1")))
                .andExpect(jsonPath("$.selections[0].lockedOdds", is(1.90)))
                .andExpect(jsonPath("$.selections[0].predictedResult", is("HOME_WIN")))

                .andExpect(jsonPath("$.selections[1].event.eventId", is("evt2")))
                .andExpect(jsonPath("$.selections[1].lockedOdds", is(2.10)))
                .andExpect(jsonPath("$.selections[1].predictedResult", is("DRAW")));

        verify(betService, times(1)).createBet(any(BetRequest.class));
    }

    @Test
    @DisplayName("GET /bets → 200 + DTO z selekcjami")
    void getBetsCreatedByUser_withSelections_thenReturnsDtoList() throws Exception {
        InternalEventDTO ev1 = new InternalEventDTO(); ev1.setEventId("evt1");
        InternalEventDTO ev2 = new InternalEventDTO(); ev2.setEventId("evt2");

        BetSelectionDTO sel1 = new BetSelectionDTO(
                1L,
                ev1,
                new BigDecimal("1.90"),
                "HOME_WIN",
                false,
                false
                );
        BetSelectionDTO sel2 = new BetSelectionDTO(
                2L,
                ev2,
                new BigDecimal("2.10"),
                "DRAW",
                false,
                false
                );

        BetDTO created = new BetDTO(
                123L,
                new BigDecimal("10.00"),
                new BigDecimal("3.50"),
                "PENDING",
                new BigDecimal("35.00"),
                List.of(sel1, sel2),
                new Date()
                );

        when(betService.getBetsCreatedByUser()).thenReturn(List.of(created));

        mockMvc.perform(get("/bets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))

                .andExpect(jsonPath("$[0].betId", is(123)))
                .andExpect(jsonPath("$[0].selections", hasSize(2)))
                .andExpect(jsonPath("$[0].selections[0].event.eventId", is("evt1")))
                .andExpect(jsonPath("$[0].selections[0].lockedOdds", is(1.90)))
                .andExpect(jsonPath("$[0].selections[0].predictedResult", is("HOME_WIN")))
                .andExpect(jsonPath("$[0].selections[1].event.eventId", is("evt2")))
                .andExpect(jsonPath("$[0].selections[1].lockedOdds", is(2.10)))
                .andExpect(jsonPath("$[0].selections[1].predictedResult", is("DRAW")));

        verify(betService, times(1)).getBetsCreatedByUser();
    }
}
