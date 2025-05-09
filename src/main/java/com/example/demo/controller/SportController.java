package com.example.demo.controller;

import com.example.demo.constants.ApplicationConstants;
import com.example.demo.dtos.OddsDTO;
import com.example.demo.dtos.ScoreDTO;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.services.SportService;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
public class SportController {
    private final SportApiProxy proxy;
    private final Environment env;
    private final SportService sportService;

    public SportController(SportApiProxy proxy, Environment env, SportService sportService) {
        this.proxy = proxy;
        this.env = env;
        this.sportService = sportService;
    }

    @GetMapping("/sports")
    public Set<Object> getMessagesByStatus(){
        return sportService.getSports();
    }
    @GetMapping("/event/odds/{sport}")
    List<OddsDTO> getOdds(@PathVariable("sport") String sport,
                          @RequestParam("regions") String regions,
                          @RequestParam("markets") String markets){
        return proxy.getOdds(sport, env.getProperty(ApplicationConstants.SPORT_API_KEY), regions,markets);
    }

    @GetMapping("/event/sport/{sport}/score")
    List<ScoreDTO> getScore(@PathVariable("sport") String sport){
        return proxy.getScore(sport, env.getProperty(ApplicationConstants.SPORT_API_KEY),1);
    }
}
