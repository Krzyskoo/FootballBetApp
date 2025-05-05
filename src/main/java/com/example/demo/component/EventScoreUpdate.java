package com.example.demo.component;

import com.example.demo.Dtos.ScoreDTO;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventScoreUpdate {
    private final SportApiProxy proxy;
    private final EventService eventService;
    private final Environment env;


    //@Scheduled(fixedRate = 36000000 ) //12h
    public void updateMatchResultDaily(){
        Set<String> sportKeys = eventService.getSportsKeysFromNonCompletedEvents();
        if (sportKeys.isEmpty())
        {
            return;
        }
        List<ScoreDTO> scores = sportKeys.stream()
                .map(sportKey -> proxy.getScore(sportKey, env.getProperty(ApplicationConstants.SPORT_API_KEY),1))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        eventService.updateMatchResult(scores);
    }



}
