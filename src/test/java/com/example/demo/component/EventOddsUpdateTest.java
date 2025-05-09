package com.example.demo.component;

import com.example.demo.dtos.BookmakersDTO;
import com.example.demo.dtos.MarketDTO;
import com.example.demo.dtos.OddsDTO;
import com.example.demo.dtos.OutcomesDTO;
import com.example.demo.constants.ApplicationConstants;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.EventRepo;
import com.example.demo.services.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventOddsUpdateTest {
    @Mock
    private EventRepo eventRepo;

    @Mock
    private EventService eventService;

    @Mock
    private SportApiProxy proxy;

    @Mock
    private Environment env;

    @InjectMocks
    private EventOddsUpdate component;

    private static final String API_KEY = "test-api-key";




    private OddsDTO makeSampleOdds(String eventId) {
        OutcomesDTO o1 = new OutcomesDTO("HOME_WIN", "1.9");
        OutcomesDTO o2 = new OutcomesDTO("AWAY_WIN", "2.1");
        MarketDTO market = new MarketDTO("h2h", "2025-05-01T12:00:00Z", List.of(o1, o2));
        BookmakersDTO bm = new BookmakersDTO("draftkings", "DraftKings", new Date(), List.of(market));
        return new OddsDTO(
                eventId,
                "soccer_epl",
                "Premier League",
                new Date(System.currentTimeMillis() + 60000),
                "TeamA",
                "TeamB",
                List.of(bm)
        );
    }

    @Test
    void updateEmptyOdds_withResults_invokesProxyAndService() {
        List<Object[]> dbResults = List.of(
                new Object[]{"soccer_epl", "evt1,evt2"},
                new Object[]{"soccer_poland_ekstraklasa", "evt3"}
        );
        when(env.getProperty(ApplicationConstants.SPORT_API_KEY)).thenReturn(API_KEY);

        when(eventRepo.getIdsGroupedBySportKey()).thenReturn(dbResults);


        OddsDTO odds1 = makeSampleOdds("evt1");
        OddsDTO odds2 = makeSampleOdds("evt2");
        OddsDTO odds3 = makeSampleOdds("evt3");

        when(proxy.updateEmptyEventOdds(
                eq("soccer_epl"), eq(API_KEY),
                eq(ApplicationConstants.SPORT_REGION),
                eq(ApplicationConstants.SPORRT_MARKET),
                eq("evt1,evt2")
        )).thenReturn(List.of(odds1, odds2));

        when(proxy.updateEmptyEventOdds(
                eq("soccer_poland_ekstraklasa"), eq(API_KEY),
                eq(ApplicationConstants.SPORT_REGION),
                eq(ApplicationConstants.SPORRT_MARKET),
                eq("evt3")
        )).thenReturn(List.of(odds3));

        component.updateEmptyOdds();

        verify(eventService).updateEventsWithEmptyOdds(List.of(odds1, odds2));
        verify(eventService).updateEventsWithEmptyOdds(List.of(odds3));
        verifyNoMoreInteractions(eventService);
    }

    @Test
    void updateEmptyOdds_noDbResults_doesNothing() {
        when(eventRepo.getIdsGroupedBySportKey()).thenReturn(Collections.emptyList());

        component.updateEmptyOdds();

        verifyNoInteractions(proxy);
        verifyNoInteractions(eventService);
    }
}
