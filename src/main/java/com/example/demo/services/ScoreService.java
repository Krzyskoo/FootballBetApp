package com.example.demo.services;

import com.example.demo.model.Event;
import com.example.demo.model.Score;
import com.example.demo.repo.ScoreRepo;
import org.springframework.stereotype.Service;

@Service
public class ScoreService{
    private final ScoreRepo scoreRepo;

    public ScoreService(ScoreRepo scoreRepo) {
        this.scoreRepo = scoreRepo;
    }

    public Score saveScore(Event event, long homeScore, long awayScore) {
        return scoreRepo.saveAndFlush(Score.builder()
                .awayTeamScore(awayScore)
                .homeTeamScore(homeScore)
                .event(event)
                .build());

    }

}
