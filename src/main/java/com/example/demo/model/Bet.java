package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long betId;

    @OneToOne
    @JoinColumn(name = "user2_id")
    private Customer user;

    private String matchId;

    private String selectedOutcome;

    private BigDecimal odds;
    private BigDecimal stake;

    private String status;
    private BigDecimal winAmount;

}
