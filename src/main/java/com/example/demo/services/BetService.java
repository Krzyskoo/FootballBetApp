package com.example.demo.services;

import com.example.demo.Dtos.BetRequest;
import com.example.demo.Dtos.BetSelectionRequest;
import com.example.demo.model.*;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.BetRepo;
import com.example.demo.repo.CustomerRepo;
import com.example.demo.repo.EventRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class BetService {
    private final CustomerRepo customerRepo;
    private final EventRepo eventRepo;
    private final BetRepo betRepo;
    private final SportApiProxy proxy;
    private final Environment env;


    @Transactional
    public Bet createBet(BetRequest betRequest) {
        Customer customer = customerRepo.findById(betRequest.getCustomerId()).orElseThrow(
                () -> new IllegalArgumentException("Customer not found"));
        if (customer.getBalance().compareTo(betRequest.getAmount()) < 0) throw new IllegalArgumentException("Insufficient balance");
        List<BetSelection> selections = new ArrayList<>();
        BigDecimal totalOdds = BigDecimal.ONE;
        for (BetSelectionRequest betSelectionRequest : betRequest.getSelections()){
            Event event = eventRepo.findById(betSelectionRequest.getEventId()).orElseThrow(
                    () -> new IllegalArgumentException("Event not found"));


            BigDecimal odds = getOdds(event,betSelectionRequest.getPredictedResult());
            totalOdds = totalOdds.multiply(odds);

            selections.add(BetSelection.builder()
                    .event(event)
                    .lockedOdds(odds)
                    .predictedResult(betSelectionRequest.getPredictedResult())
                    .completed(false)
                    .build());
        }
        BigDecimal potentialWinAmount = betRequest.getAmount().multiply(totalOdds);
        Bet bet = Bet.builder()
                .user(customer)
                .selections(selections)
                .totalOdds(totalOdds)
                .winAmount(potentialWinAmount)
                .stake(betRequest.getAmount())
                .status("PENDING")
                .build();
        for (BetSelection selection : selections){
            selection.setBet(bet);
        }
        customer.setBalance(customer.getBalance().subtract(betRequest.getAmount()));
        customerRepo.save(customer);
        return betRepo.save(bet);
    }

    private BigDecimal getOdds(Event event, Result predictedResult) {
        return switch (predictedResult) {
            case HOME_WIN -> new BigDecimal(event.getHomeTeamOdds());
            case AWAY_WIN -> new BigDecimal(event.getAwayTeamOdds());
            default -> throw new IllegalArgumentException("Invalid predicted result");
        };
    }
}
