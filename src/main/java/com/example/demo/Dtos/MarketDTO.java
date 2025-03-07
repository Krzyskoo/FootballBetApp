package com.example.demo.Dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketDTO {
    @JsonProperty("key")
    private String key;
    @JsonProperty("last_update")
    private String lastUpdate;
    @JsonProperty("outcomes")
    private List<OutcomesDTO> outcomes;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<OutcomesDTO> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(List<OutcomesDTO> outcomes) {
        this.outcomes = outcomes;
    }
}
