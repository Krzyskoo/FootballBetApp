package com.example.demo.repo;

import com.example.demo.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BetRepo extends JpaRepository<Bet, Long> {

}
