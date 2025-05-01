package com.example.demo.service;

import com.example.demo.model.BetSelection;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.repo.BetSelectionRepo;
import com.example.demo.services.BetSelectionService;
import com.example.demo.services.BetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BetSelectionServiceTest {
    @Mock
    private BetSelectionRepo betSelectionRepo;

    @Mock
    private BetService betService;

    @InjectMocks
    private BetSelectionService betSelectionService;

    @Test
    void updateBetSelections_shouldUpdateAndTriggerBetUpdate() {
        // Given
        Event event = Event.builder()
                .eventId("EVT1")
                .completed(false)
                .build();

        BetSelection sel1 = BetSelection.builder()
                .id(1L)
                .event(event)
                .completed(false)
                .predictedResult(Result.HOME_WIN)
                .build();

        BetSelection sel2 = BetSelection.builder()
                .id(2L)
                .event(event)
                .completed(false)
                .predictedResult(Result.DRAW)
                .build();

        List<BetSelection> selections = List.of(sel1, sel2);

        when(betSelectionRepo.findAllByEvent(event)).thenReturn(selections);

        // When
        betSelectionService.updateBetSelections(event, Result.HOME_WIN);

        // Then
        assertTrue(sel1.isCompleted());
        assertTrue(sel1.isWon());

        assertTrue(sel2.isCompleted());
        assertFalse(sel2.isWon());

        verify(betSelectionRepo).saveAllAndFlush(selections);
        verify(betService).updateBetAfterUpdateBetSelections(selections);
    }
}
