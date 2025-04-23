package com.example.demo.Dtos;

import com.example.demo.model.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InternalEventDTO {
    private String eventId;
    private String sportKey;
    private String sportTitle;
    private Date startTime;
    private String homeTeam;
    private String homeTeamOdds;
    private String awayTeam;
    private String awayTeamOdds;
    private String drawOdds;
    private Result status;
    private boolean completed;

}
