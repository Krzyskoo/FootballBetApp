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
public class BetSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // do jakiego kuponu należy
    @ManyToOne
    private Bet bet;

    private String homeTeam;
    private String awayTeam;

    // kluczowe informacje o meczu
    @OneToOne
    @JoinColumn(name = "event_id")
    private Event eventId;       // match id
    private BigDecimal lockedOdds; // 1.90
    @Enumerated(EnumType.STRING)
    private Result predictedResult;

    private boolean completed;

}
