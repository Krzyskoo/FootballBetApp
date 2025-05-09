package com.example.demo.services;

import com.example.demo.dtos.*;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.model.Score;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.EventRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
@Slf4j
@Service
public class EventService {
    private final SportApiProxy proxy;
    private final Environment env;
    private final EventRepo eventRepo;
    private final BetSelectionService betSelectionService;
    private final ScoreService scoreService;

    public EventService(SportApiProxy proxy, Environment env, EventRepo eventRepo, BetSelectionService betSelectionService, ScoreService scoreService) {
        this.proxy = proxy;
        this.env = env;
        this.eventRepo = eventRepo;
        this.betSelectionService = betSelectionService;
        this.scoreService = scoreService;
    }

    public void getEventsForSports(String sport) {
       List<EventsDTO> getEventInfo = proxy.getEvents(env.getProperty(ApplicationConstants.SPORT_API_KEY), sport);
       List<OddsDTO> getEventOdds = proxy.getOdds(sport,
               env.getProperty(ApplicationConstants.SPORT_API_KEY),
               ApplicationConstants.SPORT_REGION,
               ApplicationConstants.SPORRT_MARKET);
       Map<String, Map<String,String>> extractOdds = extractOdds(getEventOdds);

       getEventInfo.stream()
               .map(event -> {
                  Event builtEvent = buildFullEvent(event, extractOdds);
                   if (!eventRepo.existsByEventId(builtEvent.getEventId())) {
                       eventRepo.save(builtEvent);
                   }
                   return builtEvent;
               })
               .collect(Collectors.toList());
    }
    private Event buildFullEvent(EventsDTO eventBase, Map<String, Map<String, String>> oddsMap) {
        Map<String, String> eventOdds = oddsMap.getOrDefault(eventBase.getId(), Collections.emptyMap());

        String homeTeamOdds = eventOdds.getOrDefault(eventBase.getHomeTeam(),"");
        String awayTeamOdds = eventOdds.getOrDefault(eventBase.getAwayTeam(),"");
        String drawOdds = eventOdds.getOrDefault("Draw","");


        return Event.builder()
                .eventId(eventBase.getId())
                .sportKey(eventBase.getSportKey())
                .sportTitle(eventBase.getSportTitle())
                .startTime(eventBase.getCommenceTime())
                .homeTeam(eventBase.getHomeTeam())
                .homeTeamOdds(homeTeamOdds)
                .awayTeam(eventBase.getAwayTeam())
                .awayTeamOdds(awayTeamOdds)
                .drawOdds(drawOdds)
                .status(Result.PENDING)
                .completed(false)
                .build();
    }
    public Map<String,Map<String, String>> extractOdds(List<OddsDTO> events) {
        log.info("Extracting Odds from API call");
        return events.stream()
                .collect(Collectors.toMap(
                        OddsDTO::getId,
                        odds -> odds.getBookmakers().stream()
                                .filter(bookmakerName -> bookmakerName.getKey().equals("draftkings"))
                                .flatMap(bookmaker -> bookmaker.getMarketDTOS().stream()
                                        .filter(marketType ->marketType.getKey().contains("h2h")))
                                .flatMap(market -> market.getOutcomes().stream())
                                .collect(Collectors
                                        .toMap(
                                                OutcomesDTO::getName,
                                                OutcomesDTO::getPrice,
                                                (existing, replacement) -> existing
                                        )),
                        (existing, replacement) -> existing
                ));



    }
    public void updateEventsWithEmptyOdds(List<OddsDTO> oddsDTO) {
        log.info("Updating events with empty odds");
        Map<String, Map<String,String>> extractOdds = extractOdds(oddsDTO);
        for (OddsDTO eventOdds: oddsDTO) {
            Map<String, String> oddsForEvent = extractOdds.getOrDefault(eventOdds.getId(), Collections.emptyMap());
            try {
                String homeOdds = oddsForEvent.getOrDefault(eventOdds.getHomeTeam(), "");
                String awayOdds = oddsForEvent.getOrDefault(eventOdds.getAwayTeam(), "");
                String drawOdds = oddsForEvent.getOrDefault("Draw", "");
                eventRepo.updateOddsByEventId(
                        eventOdds.getId(),
                        homeOdds,
                        awayOdds,
                        drawOdds);
                log.info("Updated odds for event {}", eventOdds.getId());
            }catch (NumberFormatException e){
                log.error("Error parsing odds for event {}", eventOdds.getId());
                System.out.println("Błąd parsowania kursu dla eventu " + eventOdds.getId());
                e.printStackTrace();
            }
        }

    }

    @Transactional
    public void updateMatchResult(List<ScoreDTO> scoreDTO){
        for (ScoreDTO result: scoreDTO) {
            Optional<Event> eventOpt = eventRepo.findById(result.getId());
            log.info("Updating match result for event {}", result.getId());
            if (eventOpt.isPresent()&&result.isCompleted()) {
                Event event = eventOpt.get();
                long homeScore = getTeamScore(result, event.getHomeTeam());
                long awayScore = getTeamScore(result, event.getAwayTeam());
                if (homeScore > awayScore) {
                    event.setStatus(Result.HOME_WIN);
                } else if (homeScore < awayScore) {
                    event.setStatus(Result.AWAY_WIN);
                } else {
                    event.setStatus(Result.DRAW);
                }
                Score score = scoreService.saveScore(event, homeScore, awayScore);
                event.setCompleted(true);
                event.setScore(score);
                eventRepo.saveAndFlush(event);
                log.info("Match result updated for event {} with result {}", result.getId(),event.getStatus());

                betSelectionService.updateBetSelections(event, event.getStatus());
            }
        }

    }
    private long getTeamScore(ScoreDTO scoreDTO, String teamName) {
        return scoreDTO.getScores().stream()
                .filter(score -> score.getName().equalsIgnoreCase(teamName))
                .map(MatchResultDTO::getScore)
                .findFirst()
                .orElse(0L);
    }
    public Set<String> getSportsKeysFromNonCompletedEvents() {
        return eventRepo.findSportKeysByEventCompleted();
    }
    public List<Event> getAllNonCompletedEvents() {
        return eventRepo.findAllByCompleted(false)
                .stream()
                .filter(event -> event.getStartTime().after(new Date()))
                .collect(Collectors.toList());
    }


}
