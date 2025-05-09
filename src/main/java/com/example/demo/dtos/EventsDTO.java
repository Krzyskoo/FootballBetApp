package com.example.demo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventsDTO {

    @JsonProperty("id")
    private String id;
    @JsonProperty("sport_key")
    private String sportKey;
    @JsonProperty("sport_title")
    private String sportTitle;
    @JsonProperty("commence_time")
    private Date commenceTime;
    @JsonProperty("home_team")
    private String homeTeam;
    @JsonProperty("away_team")
    private String awayTeam;

}