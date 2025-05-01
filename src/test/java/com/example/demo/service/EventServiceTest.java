package com.example.demo.service;

import com.example.demo.Dtos.*;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.model.Score;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.EventRepo;
import com.example.demo.services.BetSelectionService;
import com.example.demo.services.EventService;
import com.example.demo.services.ScoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {
    @Mock SportApiProxy proxy;
    @Mock Environment env;
    @Mock EventRepo eventRepo;
    @Mock BetSelectionService betSelectionService;
    @Mock ScoreService scoreService;

    @InjectMocks EventService service;

    @Test
    void shouldSaveEventWhenNotExists() {
        String sport = "soccer";
        when(env.getProperty(anyString())).thenReturn("dummyKey");

        EventsDTO eventDTO = new EventsDTO(
                "1","soccer","Soccer",
                new Date(System.currentTimeMillis()+10_000),
                "TeamA","TeamB"
        );
        when(proxy.getEvents(anyString(), eq(sport)))
                .thenReturn(List.of(eventDTO));

        OutcomesDTO h = new OutcomesDTO("TeamA","1.5");
        OutcomesDTO a = new OutcomesDTO("TeamB","2.5");
        OutcomesDTO d = new OutcomesDTO("Draw","3.0");
        MarketDTO m = new MarketDTO("h2h","unused", List.of(h,a,d));
        BookmakersDTO b = new BookmakersDTO("draftkings","DK", new Date(), List.of(m));
        OddsDTO oddsDTO = new OddsDTO(
                "1","soccer","Soccer",
                new Date(System.currentTimeMillis()+10_000),
                "TeamA","TeamB",
                List.of(b)
        );
        when(proxy.getOdds(eq(sport), anyString(), anyString(), anyString()))
                .thenReturn(List.of(oddsDTO));

        when(eventRepo.existsByEventId("1")).thenReturn(false);

        // when
        service.getEventsForSports(sport);

        // then
        verify(eventRepo, times(1)).save(argThat(e ->
                e.getEventId().equals("1") &&
                        e.getHomeTeamOdds().equals("1.5") &&
                        e.getAwayTeamOdds().equals("2.5") &&
                        e.getDrawOdds().equals("3.0") &&
                        e.getStatus() == Result.PENDING
        ));
    }
    @Test
    void shouldExtractOddsCorrectly() {
        // given
        OutcomesDTO o1 = new OutcomesDTO("Home", "1.5");
        OutcomesDTO o2 = new OutcomesDTO("Away", "2.5");
        OutcomesDTO od = new OutcomesDTO("Draw", "3.0");
        MarketDTO m = new MarketDTO("h2h", "unused", List.of(o1, o2, od));
        BookmakersDTO b = new BookmakersDTO("draftkings", "DK", new Date(), List.of(m));
        OddsDTO dto = new OddsDTO("e1", "sport", "title", new Date(), "Home", "Away", List.of(b));

        // when
        Map<String, Map<String, String>> result = service.extractOdds(List.of(dto));

        // then
        assertTrue(result.containsKey("e1"));
        Map<String, String> odds = result.get("e1");
        assertEquals("1.5", odds.get("Home"));
        assertEquals("2.5", odds.get("Away"));
        assertEquals("3.0", odds.get("Draw"));
    }
    @Test
    void shouldUpdateEventsWithEmptyOdds() {
        // given
        OutcomesDTO o1 = new OutcomesDTO("Home", "1.5");
        OutcomesDTO o2 = new OutcomesDTO("Away", "2.5");
        OutcomesDTO od = new OutcomesDTO("Draw", "3.0");
        MarketDTO m = new MarketDTO("h2h", "unused", List.of(o1, o2, od));
        BookmakersDTO b = new BookmakersDTO("draftkings", "DK", new Date(), List.of(m));
        OddsDTO dto = new OddsDTO("e1", "sport", "title", new Date(), "Home", "Away", List.of(b));

        // when
        service.updateEventsWithEmptyOdds(List.of(dto));

        // then
        verify(eventRepo).updateOddsByEventId("e1", "1.5", "2.5", "3.0");
    }
    @Test
    void shouldUpdateMatchResult() {
        // given
        Event event = Event.builder()
                .eventId("e1")
                .homeTeam("T1")
                .awayTeam("T2")
                .status(Result.PENDING)
                .completed(false)
                .build();
        when(eventRepo.findById("e1")).thenReturn(Optional.of(event));

        MatchResultDTO mr1 = new MatchResultDTO("T1", 2L);
        MatchResultDTO mr2 = new MatchResultDTO("T2", 1L);
        ScoreDTO scoreDto = new ScoreDTO();
        scoreDto.setId("e1");
        scoreDto.setCompleted(true);
        scoreDto.setScores(List.of(mr1, mr2));

        Score savedScore = new Score();
        when(scoreService.saveScore(eq(event), eq(2L), eq(1L))).thenReturn(savedScore);

        // when
        service.updateMatchResult(List.of(scoreDto));

        // then
        assertTrue(event.isCompleted());
        assertEquals(Result.HOME_WIN, event.getStatus());
        verify(scoreService).saveScore(event, 2L, 1L);
        verify(eventRepo).saveAndFlush(event);
        verify(betSelectionService).updateBetSelections(event, Result.HOME_WIN);
    }
    @Test
    void shouldGetSportsKeysFromNonCompletedEvents() {
        // given
        Set<String> keys = Set.of("football", "basketball");
        when(eventRepo.findSportKeysByEventCompleted()).thenReturn(keys);

        // when
        Set<String> result = service.getSportsKeysFromNonCompletedEvents();

        // then
        assertEquals(keys, result);
    }

    @Test
    void shouldReturnOnlyFutureNonCompletedEvents() {
        // given
        Event past = Event.builder()
                .eventId("p1").startTime(new Date(System.currentTimeMillis() - 10_000)).completed(false).build();
        Event future = Event.builder()
                .eventId("f1").startTime(new Date(System.currentTimeMillis() + 10_000)).completed(false).build();
        when(eventRepo.findAllByCompleted(false)).thenReturn(List.of(past, future));

        // when
        List<Event> result = service.getAllNonCompletedEvents();

        // then
        assertEquals(1, result.size());
        assertEquals("f1", result.get(0).getEventId());
    }
}
