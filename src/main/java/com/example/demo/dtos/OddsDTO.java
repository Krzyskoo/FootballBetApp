package com.example.demo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OddsDTO extends EventsDTO {

    @JsonProperty("bookmakers")
    private List<BookmakersDTO> bookmakers;
    public OddsDTO(String id, String sportKey, String sportTitle, Date commenceTime,
                   String homeTeam, String awayTeam, List<BookmakersDTO> bookmakers) {
        super(id, sportKey, sportTitle, commenceTime, homeTeam, awayTeam);
        this.bookmakers = bookmakers;
    }
}
