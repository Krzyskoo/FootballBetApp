package com.example.demo.services;

import com.example.demo.Dtos.BetDTO;
import com.example.demo.Dtos.BetRequest;
import com.example.demo.Dtos.BetSelectionRequest;
import com.example.demo.mapper.BetMapper;
import com.example.demo.model.*;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.BetRepo;
import com.example.demo.repo.CustomerRepo;
import com.example.demo.repo.EventRepo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class BetService {
    private final CustomerRepo customerRepo;
    private final CustomerService customerService;
    private final EventRepo eventRepo;
    private final BetRepo betRepo;
    private final SportApiProxy proxy;
    private final Environment env;
    private final BetMapper betMapper;
    private final BalanceHistoryService balanceHistoryService;


    @Transactional
    public Bet createBet(BetRequest betRequest) {
        Customer customer = customerRepo.findById(customerService.getAuthenticatedUsername()).orElseThrow(
                () -> new IllegalArgumentException("Customer not found")
        );
        if (customer.getBalance().compareTo(betRequest.getAmount()) < 0) throw new IllegalArgumentException("Insufficient balance");
        List<BetSelection> selections = new ArrayList<>();
        BigDecimal totalOdds = BigDecimal.ONE;
        for (BetSelectionRequest betSelectionRequest : betRequest.getSelections()){
            Event event = eventRepo.findById(betSelectionRequest.getEventId()).orElseThrow(
                    () -> new IllegalArgumentException("Event not found"));
            if (event.getStartTime().before(new java.util.Date())) throw new IllegalArgumentException("Event has already started");


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
        balanceHistoryService.saveBalanceChange(customer,TransactionType.BET_PLACED,bet.getStake(),"Bet placed" );
        return betRepo.save(bet);
    }

    public BigDecimal getOdds(Event event, Result predictedResult) {
        return switch (predictedResult) {
            case HOME_WIN -> new BigDecimal(event.getHomeTeamOdds());
            case AWAY_WIN -> new BigDecimal(event.getAwayTeamOdds());
            case DRAW -> new BigDecimal(event.getDrawOdds());
            default -> throw new IllegalArgumentException("Invalid predicted result");
        };
    }

    public void updateBetAfterUpdateBetSelections(List<BetSelection>selections) {
        log.info("Updating bet selections results");
        selections.forEach(
                selection -> betRepo.findBySelectionsId(selection.getId()).ifPresent(bet -> {
                        boolean allCompleted = bet.getSelections().stream().allMatch(BetSelection::isCompleted);
                        boolean allWon = bet.getSelections().stream().allMatch(BetSelection::isWon);
                        if (allCompleted && allWon) {
                            bet.setStatus("WON");
                            log.info("Bet won by user {}", bet.getUser().getId());
                            updateBalanceAfterBetWin(bet);
                        }else if (allCompleted && !allWon) {
                            log.info("Bet lost by user {}", bet.getUser().getId());
                            bet.setStatus("LOST");
                        }
                        betRepo.save(bet);
                    }
                )
        );


    }
    public void updateBalanceAfterBetWin(Bet bet) {
        log.info("Updating balance after bet win");
        Customer customer = customerRepo.findById(bet.getUser().getId()).orElseThrow(
                () -> new IllegalArgumentException("Customer not found")
        );
        balanceHistoryService.saveBalanceChange(customer,TransactionType.BET_WON,bet.getWinAmount(),"Bet won" );
    }
    public List<BetDTO> getBetsCreatedByUser(){
        List<Bet> bets = betRepo.findAllByUserId(customerService.getAuthenticatedUsername())
                .orElseThrow(() -> new IllegalArgumentException("Bets not found"));
        return betMapper.toDtoList(bets).stream().sorted(Comparator.comparing(BetDTO::getCreatedDt).reversed()).toList();

    }
}
