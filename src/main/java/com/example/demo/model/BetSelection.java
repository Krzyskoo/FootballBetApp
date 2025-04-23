package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BetSelection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    @ManyToOne
    @JoinColumn(name = "bet_id", nullable = false)
    @JsonBackReference
    private Bet bet;

    @Column(nullable = false)
    private BigDecimal lockedOdds; // 1.90

    @Enumerated(EnumType.STRING)
    private Result predictedResult;

    private boolean isWon;

    private boolean completed;



}
