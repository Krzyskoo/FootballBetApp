package com.example.demo.dtos;

import com.example.demo.model.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BetSelectionRequest {
    private String eventId;
    private Result predictedResult;

}
