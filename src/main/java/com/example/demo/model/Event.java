package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Event {

    @Id
    private String eventId;
    private String sportKey;
    private String sportTitle;
    private Date startTime;
    private String homeTeam;
    private String homeTeamOdds;
    private String awayTeam;
    private String awayTeamOdds;
    @Enumerated(EnumType.STRING)
    private Result status;
    private boolean completed;

    @JsonIgnore
    @OneToMany(mappedBy = "event")
    private List<BetSelection> betSelection;

    @Builder
    public Event(String eventId, String sportKey, String sportTitle, Date startTime,
                 String homeTeam, String homeTeamOdds, String awayTeam,
                 String awayTeamOdds, Result status, boolean completed) {
        this.eventId = eventId;
        this.sportKey = sportKey;
        this.sportTitle = sportTitle;
        this.startTime = startTime;
        this.homeTeam = homeTeam;
        this.homeTeamOdds = homeTeamOdds;
        this.awayTeam = awayTeam;
        this.awayTeamOdds = awayTeamOdds;
        this.status = status;
        this.completed = completed;
    }


}
