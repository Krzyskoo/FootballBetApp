package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private BigDecimal totalOdds;
    private BigDecimal stake;

    private String status;
    private BigDecimal winAmount;
    @CreatedDate
    @JsonIgnore
    private Date createdDt;

    @OneToMany(mappedBy = "bet", cascade = CascadeType.ALL)
    private List<BetSelection> selections = new  ArrayList<>();

}
