package com.example.demo.services;

import com.example.demo.model.BetSelection;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.repo.BetSelectionRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class BetSelectionService {

   private final BetSelectionRepo betSelectionRepo;
   private final BetService betService;

    public BetSelectionService(BetSelectionRepo betSelectionRepo, BetService betService) {
        this.betSelectionRepo = betSelectionRepo;
        this.betService = betService;
    }

    public void updateBetSelections(Event event, Result actualResult) {
        log.info("Updating bet selections for event {}", event.getEventId());
        List<BetSelection> selections = betSelectionRepo.findAllByEvent(event);
        selections.stream().forEach(selection-> {
            if (!selection.isCompleted()) {
                selection.setCompleted(true);
                boolean isCorrect = selection.getPredictedResult().equals(actualResult);
                selection.setWon(isCorrect);
            }
        });
        betSelectionRepo.saveAllAndFlush(selections);
        betService.updateBetAfterUpdateBetSelections(selections);
    }


}
