package com.example.demo.proxy;

import com.example.demo.dtos.EventsDTO;
import com.example.demo.dtos.OddsDTO;
import com.example.demo.dtos.ScoreDTO;
import com.example.demo.dtos.SportDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "sport-api", url = "https://api.the-odds-api.com/v4")
public interface SportApiProxy {

    @RequestMapping(method = RequestMethod.GET, value = "sports")
    @Headers(value = "Content-Type: application/json")
    List<SportDTO> getSports(@RequestParam("apiKey") String apiKey);


    @RequestMapping(method = RequestMethod.GET, value = "sports/{sport}/events")
    @Headers(value = "Content-Type: application/json")
    List<EventsDTO> getEvents(@RequestParam("apiKey") String apiKey, @PathVariable("sport") String sport);

    @RequestMapping(method = RequestMethod.GET, value = "sports/{sport}/odds")
    @Headers(value = "Content-Type: application/json")
    List<OddsDTO> updateEmptyEventOdds(@PathVariable("sport") String sport,
                                 @RequestParam("apiKey") String apiKey,
                                 @RequestParam("regions") String regions,
                                 @RequestParam(value = "markets",required = false) String markets,
                                 @RequestParam("eventIds") String eventIds);

    @RequestMapping(method = RequestMethod.GET, value = "sports/{sport}/odds")
    @Headers(value = "Content-Type: application/json")
    List<OddsDTO> getOdds(@PathVariable("sport") String sport,
                            @RequestParam("apiKey") String apiKey,
                            @RequestParam("regions") String regions,
                            @RequestParam(value = "markets",required = false) String markets);
    @RequestMapping(method = RequestMethod.GET, value = "sports/{sport}/scores")
    @Headers(value = "Content-Type: application/json")
    List<ScoreDTO> getScore(@PathVariable("sport") String sport,
                            @RequestParam("apiKey") String apiKey,
                            @RequestParam("daysFrom") int daysFrom);
}
