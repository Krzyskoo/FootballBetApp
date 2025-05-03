package com.example.demo.component;

import com.example.demo.Dtos.MatchResultDTO;
import com.example.demo.Dtos.ScoreDTO;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.EventRepo;
import com.example.demo.services.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EventScoreUpdateTest {
    @Mock
    private SportApiProxy proxy;

    @Mock
    private EventService eventService;

    @Mock
    private Environment env;
    @Mock
    private EventRepo eventRepo;

    @InjectMocks
    private EventScoreUpdate component;


    @Test
    void updateMatchResultDaily_whenNoSportKeys_thenThrows() {
        when(eventService.getSportsKeysFromNonCompletedEvents())
                .thenReturn(Set.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> component.updateMatchResultDaily()
        );
        assertEquals("No events to update", ex.getMessage());

        verifyNoInteractions(proxy);

    }

    @Test
    void updateMatchResultDaily_withTwoSportKeys_thenMergedScoresPassed() {
        Set<String> keys = Set.of("soccer_poland_ekstraklasa", "soccer_belgium_first_div");
        when(eventService.getSportsKeysFromNonCompletedEvents()).thenReturn(keys);
        Event event = new Event(
                "id1",
                "soccer_poland_ekstraklasa",
                "Ekstraklasa_Poland",
                new Date(System.currentTimeMillis() - 10),
                "HT",
                "3.5",
                "AT",
                "2",
                "5",
                Result.PENDING,
                false,
                null,
                null);
        Event event2 = new Event(
                "id2",
                "soccer_belgium_first_div",
                "Belgium_First_Div",
                new Date(System.currentTimeMillis() -10),
                "HT",
                "3",
                "AT",
                "2.8",
                "2.88",
                Result.PENDING,
                false,
                null,
                null);
        eventRepo.saveAll(List.of(event, event2));

        // 2) przygotuj dwie listy wyników
        ScoreDTO s1 = new ScoreDTO("id1",
                "soccer_poland_ekstraklasa",
                "Ekstraklasa_Poland",
                new Date(),
                true,
                "HT",
                "AT",
                List.of(new MatchResultDTO("HT", 3L),
                        new MatchResultDTO("AT", 2L)),
                new Date());
        ScoreDTO s2 = new ScoreDTO("id2",
                "soccer_belgium_first_div",
                "Belgium_First_Div",
                new Date(),
                true,
                "HT2",
                "AT2",
                List.of(new MatchResultDTO("HT2", 1L),
                        new MatchResultDTO("AT2", 2L)),
                new Date());
        when(env.getProperty(ApplicationConstants.SPORT_API_KEY)).thenReturn("dummy-key");
        when(proxy.getScore("soccer_poland_ekstraklasa", "dummy-key", 1)).thenReturn(List.of(s1));
        when(proxy.getScore("soccer_belgium_first_div",        "dummy-key", 1)).thenReturn(List.of(s2));

        component.updateMatchResultDaily();

        verify(proxy).getScore("soccer_poland_ekstraklasa", "dummy-key", 1);
        verify(proxy).getScore("soccer_belgium_first_div",        "dummy-key", 1);

        ArgumentCaptor<List<ScoreDTO>> captor = ArgumentCaptor.forClass(List.class);
        verify(eventService).updateMatchResult(captor.capture());

        List<ScoreDTO> merged = captor.getValue();
        assertThat(merged).containsExactlyInAnyOrder(s1, s2);

        verifyNoMoreInteractions(eventService);
    }
}
