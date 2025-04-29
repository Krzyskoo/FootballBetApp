package com.example.demo.repo;

import com.example.demo.model.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
public class EventRepoTest {
    @Autowired
    private EventRepo eventRepo;

    @Test
    void shouldReturnTrueWhenEventExists() {
        Event event = createEvent();
        eventRepo.save(event);
        boolean exists = eventRepo.existsByEventId(event.getEventId());
        assertThat(exists).isTrue();
    }
    @Test
    void shouldReturnEventsWithEmptyOdds() {
        Event event = createEvent();
        eventRepo.saveAndFlush(event);
        List<Object[]> eventsWithEmptyOdds = eventRepo.getIdsGroupedBySportKey();
        assertThat(eventsWithEmptyOdds).isNotEmpty();
    }
    @Test
    void shouldUpdateOddsByEventId() {
        Event event = createEvent();
        eventRepo.saveAndFlush(event);
        Event event1 = eventRepo.findById(event.getEventId()).get();
        event1.setHomeTeamOdds("1.1");
        event1.setAwayTeamOdds("1.2");
        event1.setDrawOdds("1.3");
        eventRepo.saveAndFlush(event1);
        Event event2 = eventRepo.findById(event.getEventId()).get();
        assertThat(event2.getHomeTeamOdds()).isEqualTo("1.1");
        assertThat(event2.getAwayTeamOdds()).isEqualTo("1.2");
        assertThat(event2.getDrawOdds()).isEqualTo("1.3");
    }
    public Event createEvent(){
        return Event.builder()
                .eventId("1")
                .sportKey("sportKey")
                .sportTitle("sportTitle")
                .completed(false)
                .startTime(new Date())
                .homeTeam("homeTeam")
                .homeTeamOdds("")
                .awayTeamOdds("")
                .drawOdds("")
                .awayTeam("awayTeam")
                .build();
    }

}
