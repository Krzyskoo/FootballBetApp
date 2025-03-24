package com.example.demo.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OddsDTO extends EventsDTO {

    @JsonProperty("bookmakers")
    private List<BookmakersDTO> bookmakers;

}
