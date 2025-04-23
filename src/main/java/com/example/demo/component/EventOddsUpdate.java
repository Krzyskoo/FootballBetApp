package com.example.demo.component;

import com.example.demo.Dtos.OddsDTO;
import com.example.demo.constans.ApplicationConstans;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.EventRepo;
import com.example.demo.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventOddsUpdate {
    private final EventRepo eventRepo;
    private final EventService eventService;
    private final SportApiProxy proxy;
    private final Environment env;

    @Scheduled(fixedRate = 36000000)
    public void updateEmptyOdds(){
        List<Object[]> results = eventRepo.getIdsGroupedBySportKey();
        Map<String, String> sportToIdsMap = new HashMap<>();
        results.stream().forEach(result -> sportToIdsMap.put((String) result[0], (String) result[1]));
        for (Map.Entry<String, String> entry : sportToIdsMap.entrySet()) {
            String sportKey = entry.getKey();
            String ids = entry.getValue();
            List<OddsDTO> odds = proxy.updateEmptyEventOdds(sportKey, env.getProperty(ApplicationConstans.SPORT_API_KEY),ApplicationConstans.SPORT_REGION,ApplicationConstans.SPORRT_MARKET,ids);
            eventService.updateEventsWithEmptyOdds(odds);
        }


    }

}
