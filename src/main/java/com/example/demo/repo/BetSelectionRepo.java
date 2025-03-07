package com.example.demo.repo;

import com.example.demo.model.BetSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BetSelectionRepo extends JpaRepository<BetSelection, Long> {
}
