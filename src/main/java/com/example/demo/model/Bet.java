package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long betId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer user;

    private BigDecimal totalOdds;
    private BigDecimal stake;

    private String status;
    private BigDecimal winAmount;
    @CreationTimestamp
    @JsonIgnore
    private Date createdDt;

    @OneToMany(mappedBy = "bet", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<BetSelection> selections = new  ArrayList<>();

}
