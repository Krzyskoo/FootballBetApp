package com.example.demo.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OddsDTO{

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

    @JsonProperty("bookmakers")
    private List<BookmakersDTO> bookmakers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSportKey() {
        return sportKey;
    }

    public void setSportKey(String sportKey) {
        this.sportKey = sportKey;
    }

    public String getSportTitle() {
        return sportTitle;
    }

    public void setSportTitle(String sportTitle) {
        this.sportTitle = sportTitle;
    }

    public Date getCommenceTime() {
        return commenceTime;
    }

    public void setCommenceTime(Date commenceTime) {
        this.commenceTime = commenceTime;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public List<BookmakersDTO> getBookmakers() {
        return bookmakers;
    }

    public void setBookmakers(List<BookmakersDTO> bookmakers) {
        this.bookmakers = bookmakers;
    }
}
