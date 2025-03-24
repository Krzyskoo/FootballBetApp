package com.example.demo.services;

import com.example.demo.Dtos.EventsDTO;
import com.example.demo.Dtos.OddsDTO;
import com.example.demo.Dtos.OutcomesDTO;
import com.example.demo.constans.ApplicationConstans;
import com.example.demo.model.Event;
import com.example.demo.model.Result;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.EventRepo;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventService {
    private final SportApiProxy proxy;
    private final Environment env;
    private final EventRepo eventRepo;

    public EventService(SportApiProxy proxy, Environment env, EventRepo eventRepo) {
        this.proxy = proxy;
        this.env = env;
        this.eventRepo = eventRepo;
    }

    public List<Event> getEventsForSports(String sport) {
       List<EventsDTO> getEventInfo = proxy.getEvents(env.getProperty(ApplicationConstans.SPORT_API_KEY), sport);
       List<OddsDTO> getEventOdds = proxy.getOdds(sport,
               env.getProperty(ApplicationConstans.SPORT_API_KEY),
               ApplicationConstans.SPORT_REGION,
               ApplicationConstans.SPORRT_MARKET);
       Map<String, Map<String,String>> extractOdds = extractOdds(getEventOdds);

       return getEventInfo.stream()
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


}
