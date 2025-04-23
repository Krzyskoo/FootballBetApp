package com.example.demo.services;

import com.example.demo.Dtos.*;
import com.example.demo.constans.ApplicationConstans;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.model.Score;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.EventRepo;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
       List<EventsDTO> getEventInfo = proxy.getEvents(env.getProperty(ApplicationConstans.SPORT_API_KEY), sport);
       List<OddsDTO> getEventOdds = proxy.getOdds(sport,
               env.getProperty(ApplicationConstans.SPORT_API_KEY),
               ApplicationConstans.SPORT_REGION,
               ApplicationConstans.SPORRT_MARKET);
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
        // 🔹 Pobieramy kursy dla eventId
        Map<String, String> eventOdds = oddsMap.getOrDefault(eventBase.getId(), Collections.emptyMap());

        // 🔹 Pobieramy kursy dla drużyn
        String homeTeamOdds = eventOdds.getOrDefault(eventBase.getHomeTeam(),"");
        String awayTeamOdds = eventOdds.getOrDefault(eventBase.getAwayTeam(),"");
        String drawOdds = eventOdds.getOrDefault("Draw","");

        // 🔹 Tworzymy finalny obiekt Event
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
    private Map<String,Map<String, String>> extractOdds(List<OddsDTO> events) {

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
            }catch (NumberFormatException e){
                System.out.println("Błąd parsowania kursu dla eventu " + eventOdds.getId());
                e.printStackTrace();
            }
        }

    }

    @Transactional
    public void updateMatchResult(List<ScoreDTO> scoreDTO){
        for (ScoreDTO result: scoreDTO) {
            Optional<Event> eventOpt = eventRepo.findById(result.getId());
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
