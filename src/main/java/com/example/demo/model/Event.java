package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    @Id
    private String eventId;
    private String sportKey;
    private String sportTitle;
    private Date startTime;
    private String homeTeam;
    private String awayTeam;
    @Enumerated(EnumType.STRING)
    private Result status;
    private boolean completed;


    @OneToOne(mappedBy = "eventId")
    private BetSelection betSelection;

}
