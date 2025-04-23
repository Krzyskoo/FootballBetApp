package com.example.demo.Dtos;

import com.example.demo.model.Bet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class BetDTO {
    private Long betId;
    private BigDecimal totalOdds;
    private BigDecimal stake;
    private String status;
    private BigDecimal winAmount;
    private List<BetSelectionDTO> selections;
    private Date createdDt;

    public BetDTO(Bet bet) {
        this.betId = bet.getBetId();
        this.totalOdds = bet.getTotalOdds();
        this.stake = bet.getStake();
        this.status = bet.getStatus();
        this.winAmount = bet.getWinAmount();
    }
}
