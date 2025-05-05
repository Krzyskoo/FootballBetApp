package com.example.demo.repo;

import com.example.demo.model.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceHistoryRepo extends JpaRepository<BalanceHistory, Long> {
}
