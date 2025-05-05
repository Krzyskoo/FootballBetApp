package com.example.demo.services;

import com.example.demo.model.BalanceHistory;
import com.example.demo.model.Customer;
import com.example.demo.model.TransactionType;
import com.example.demo.repo.BalanceHistoryRepo;
import com.example.demo.repo.CustomerRepo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceHistoryService {

    private final CustomerRepo customerRepo;
    private final BalanceHistoryRepo balanceHistoryRepo;

    public BalanceHistoryService(CustomerRepo customerRepo, BalanceHistoryRepo balanceHistoryRepo) {
        this.customerRepo = customerRepo;
        this.balanceHistoryRepo = balanceHistoryRepo;
    }

    public void saveBalanceChange(Customer customer, TransactionType type, BigDecimal amount, String description) {

        BigDecimal oldBalance = customer.getBalance();
        BigDecimal newBalance = type == TransactionType.BET_PLACED || type == TransactionType.WITHDRAWAL
                ? oldBalance.subtract(amount)
                : oldBalance.add(amount);

        customer.setBalance(newBalance);
        customerRepo.save(customer);

        BalanceHistory history = new BalanceHistory();
        history.setCustomer(customer);
        history.setType(type);
        history.setAmount(amount);
        history.setBalanceBefore(oldBalance);
        history.setBalanceAfter(newBalance);
        history.setDescription(description);

        balanceHistoryRepo.save(history);
    }


}
