package com.example.demo.controller;

import com.example.demo.Dtos.OddsDTO;
import com.example.demo.Dtos.ScoreDTO;
import com.example.demo.Dtos.SportDTO;
import com.example.demo.constans.ApplicationConstans;
import com.example.demo.proxy.SportApiProxy;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SportController {


    private final SportApiProxy proxy;
    private final Environment env;

    public SportController(SportApiProxy proxy, Environment env) {
        this.proxy = proxy;
        this.env = env;
    }

    @GetMapping("/sports")
    public List<SportDTO> getMessagesByStatus(){
        return proxy.getSports(env.getProperty(ApplicationConstans.SPORT_API_KEY));
    }
    @GetMapping("/event/odds/{sport}")
    List<OddsDTO> getOdds(@PathVariable("sport") String sport,
                          @RequestParam("regions") String regions,
                            @RequestParam("markets") String markets){
        return proxy.getOdds(sport, env.getProperty(ApplicationConstans.SPORT_API_KEY), regions,markets);
    }

    @GetMapping("/event/sport/{sport}/score")
    List<ScoreDTO> getScore(@PathVariable("sport") String sport){
        return proxy.getScore(sport, env.getProperty(ApplicationConstans.SPORT_API_KEY),1);
    }

}





