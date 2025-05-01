package com.example.demo.service;

import com.example.demo.model.Event;
import com.example.demo.model.Score;
import com.example.demo.repo.ScoreRepo;
import com.example.demo.services.ScoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ScoreServiceTest {

    @Mock
    private ScoreRepo scoreRepo;

    @InjectMocks
    private ScoreService scoreService;

    @Test
    void shouldSaveAndFlushScore() {
        // Given
        Event event = Event.builder()
                .eventId("abc123")
                .homeTeam("Team A")
                .awayTeam("Team B")
                .completed(false)
                .build();

        Score expectedScore = Score.builder()
                .homeTeamScore(2L)
                .awayTeamScore(1L)
                .event(event)
                .build();

        when(scoreRepo.saveAndFlush(any(Score.class))).thenReturn(expectedScore);

        // When
        Score result = scoreService.saveScore(event, 2L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getHomeTeamScore());
        assertEquals(1L, result.getAwayTeamScore());
        assertEquals(event, result.getEvent());
        verify(scoreRepo).saveAndFlush(any(Score.class));
    }

}
