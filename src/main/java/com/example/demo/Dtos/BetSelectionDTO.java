package com.example.demo.Dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class BetSelectionDTO {
    private Long id;
    private InternalEventDTO event;
    private BigDecimal lockedOdds;
    private String predictedResult;
    private boolean isWon;
    private boolean completed;
}
