package com.example.demo.base;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Date;

@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date createdAt;
    private Date updatedAt;

    @PrePersist
    public void onPrePersist() {
        this.createdAt = Date.from(Instant.now());
        this.updatedAt = Date.from(Instant.now());
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = Date.from(Instant.now());
    }
}
