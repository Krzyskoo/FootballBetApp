package com.example.demo.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScoreDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("sport_key")
    private String sportKey;


    @JsonProperty("sport_title")
    private String sportTitle;

    @JsonProperty("commence_time")
    private Date commenceTime;

    @JsonProperty("completed")
    private boolean isCompleted;

    @JsonProperty("home_team")
    private String homeTeam;

    @JsonProperty("away_team")
    private String awayTeam;

    private List<MatchResultDTO> scores;

    @JsonProperty("last_update")
    private Date lastUpdate;

}
