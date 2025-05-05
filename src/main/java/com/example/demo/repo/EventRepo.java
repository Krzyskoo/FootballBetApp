package com.example.demo.repo;

import com.example.demo.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepo extends JpaRepository<Event, String> {
    boolean existsByEventId(String eventId);
    @Query(value = "select e.sportKey from Event e where e.completed = false group by e.sportKey")
    Set<String> findSportKeysByEventCompleted();
    List<Event> findAllByCompleted(boolean completed);

    @Query(value = "SELECT sport_key, GROUP_CONCAT(event_id SEPARATOR ',') AS ids " +
            "FROM event " +
            "WHERE away_team_odds ='' AND home_team_odds = '' AND draw_odds =''" +
            "GROUP BY sport_key",
            nativeQuery = true)
    List<Object[]> getIdsGroupedBySportKey();

    @Transactional
    @Modifying
    @Query(value ="UPDATE Event e SET e.homeTeamOdds = :homeOdds, e.awayTeamOdds = :awayOdds, e.drawOdds = :drawOdds WHERE e.eventId = :eventId" )
    int updateOddsByEventId(@Param("eventId") String eventId,
                            @Param("homeOdds") String homeOdds,
                            @Param("awayOdds") String awayOdds,
                            @Param("drawOdds") String drawOdds);
}
