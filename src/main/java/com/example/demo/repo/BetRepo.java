package com.example.demo.repo;

import com.example.demo.model.Bet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BetRepo extends JpaRepository<Bet, Long> {
    Optional<Bet> findBySelectionsId(Long id);
    Optional<List<Bet>> findAllByUserId(Long id);

}
