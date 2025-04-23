package com.example.demo.repo;

import com.example.demo.model.BetSelection;
import com.example.demo.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BetSelectionRepo extends JpaRepository<BetSelection, Long> {
    List<BetSelection> findAllByEvent(Event event);
}
