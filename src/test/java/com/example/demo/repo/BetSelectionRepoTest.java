package com.example.demo.repo;

import com.example.demo.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class BetSelectionRepoTest {
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private EventRepo eventRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private BetSelectionRepo betSelectionRepo;

    @Test
    public void shouldFindAllBetSelectionsByEventId() {
        Event event = createEvent();
        Customer customer = createCustomer();
        Bet bet = createBet();
        bet.setUser(customer);

        BetSelection betSelection = new BetSelection();
        betSelection.setEvent(event);
        betSelection.setBet(bet);
        betSelection.setLockedOdds(BigDecimal.valueOf(1.5));
        betSelection.setPredictedResult(Result.PENDING);

        betRepo.save(bet);
        betSelectionRepo.save(betSelection);

        List<BetSelection> betSelections = betSelectionRepo.findAllByEvent(event);
        assert(betSelections.size() == 1);
    }

    public Bet createBet() {
        Bet bet = new Bet();
        bet.setStake(BigDecimal.valueOf(100));
        bet.setTotalOdds(BigDecimal.valueOf(1.5));
        bet.setWinAmount(BigDecimal.valueOf(150));
        bet.setStatus("PENDING");
        return bet;

    }
    public Event createEvent() {
        Event event = new Event();
        event.setEventId("123");
        event.setSportKey("sportKey");
        event.setSportTitle("sportTitle");
        event.setCompleted(false);
        event.setStartTime(new Date());
        event.setHomeTeam("homeTeam");
        event.setHomeTeamOdds("1.5");
        event.setAwayTeamOdds("1.5");
        event.setDrawOdds("1.5");
        eventRepo.save(event);
        return event;
    }


    public Customer createCustomer() {
        Customer customer = new Customer();
        customer.setEmail("another@example.com");
        customer.setPassword("password");
        customerRepo.save(customer);
        return customer;
    }






}
