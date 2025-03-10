package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;
    private String email;
    private String password;
    private BigDecimal balance;
    @OneToMany(mappedBy = "customer")
    private List<Payment> payments;
    @JsonIgnore
    private Date createdDt;



    @OneToMany(mappedBy ="customer",fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Authority> authorities;

}
