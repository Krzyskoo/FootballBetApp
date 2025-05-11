package com.example.demo.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(
        name        = "MatchResultDTO",
        description = "Represents the result of a match entry, containing the participant’s name and their score."
)
public class MatchResultDTO {
    @Schema(
            description = "Name of the participant or team",
            example     = "Team A"
    )
    private String name;
    @Schema(
            description = "Score achieved by the participant",
            example     = "3"
    )
    private Long score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
