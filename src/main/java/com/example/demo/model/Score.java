package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;
    private long homeTeamScore;
    private long awayTeamScore;
}
