package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stripePaymentId;  // ID Stripe, np. pi_12345
    private BigDecimal amount;       // Kwota np. 100.00 USD
    private String currency;         // np. "USD"

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;    // "PENDING", "SUCCEEDED", "FAILED"

    @ManyToOne
    @JoinColumn(name = "customer_id",nullable = false)
    @JsonBackReference
    private Customer customer;      // ID użytkownika w systemie
    private String customerEmail;        // E-mail użytkownika

    private String paymentMethod;    // "card", "google_pay", "link"

    @CreationTimestamp
    @JsonIgnore
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @JsonIgnore
    private LocalDateTime updatedAt;


}
