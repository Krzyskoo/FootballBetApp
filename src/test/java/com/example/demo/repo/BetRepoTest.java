package com.example.demo.repo;

import com.example.demo.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
public class BetRepoTest {
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private CustomerRepo customerRepo;
    @Autowired
    private BetSelectionRepo betSelectionRepo;
    @Autowired
    private EventRepo eventRepo;
    @Test
    @DisplayName("should find Bet by Selection ID")
    void shouldFindBetBySelectionsId() {
        // given: najpierw utwórzmy Event
        Event event = Event.builder()
                .eventId("event-123")
                .sportKey("soccer_epl")
                .sportTitle("Soccer EPL")
                .startTime(new Date())
                .homeTeam("Team A")
                .awayTeam("Team B")
                .homeTeamOdds("2.5")
                .awayTeamOdds("3.0")
                .drawOdds("3.5")
                .status(Result.PENDING)
                .completed(false)
                .build();
        event = eventRepo.save(event);

        // tworzymy użytkownika
        Customer customer = Customer.builder()
                .email("user@example.com")
                .password("password")
                .balance(BigDecimal.valueOf(500))
                .build();
        customer = customerRepo.save(customer);

        // tworzymy zakład (Bet)
        Bet bet = Bet.builder()
                .stake(BigDecimal.valueOf(100))
                .totalOdds(BigDecimal.valueOf(2.5))
                .winAmount(BigDecimal.valueOf(250))
                .user(customer)
                .build();
        bet = betRepo.save(bet);

        // tworzymy wybór (BetSelection)
        BetSelection selection = BetSelection.builder()
                .event(event)
                .bet(bet)
                .lockedOdds(BigDecimal.valueOf(2.5))
                .predictedResult(Result.PENDING)
                .isWon(false)
                .completed(false)
                .build();
        selection = betSelectionRepo.save(selection);

        // when
        Optional<Bet> foundBet = betRepo.findBySelectionsId(selection.getId());

        // then
        assertThat(foundBet).isPresent();
        assertThat(foundBet.get().getBetId()).isEqualTo(bet.getBetId());
    }
    @Test
    void shouldFindAllBetsByUserId() {
        // given: użytkownik
        Customer customer = Customer.builder()
                .email("another@example.com")
                .password("password")
                .balance(BigDecimal.valueOf(1000))
                .build();
        customer = customerRepo.save(customer);

        // tworzymy kilka zakładów
        Bet bet1 = Bet.builder()
                .stake(BigDecimal.valueOf(50))
                .totalOdds(BigDecimal.valueOf(1.8))
                .winAmount(BigDecimal.valueOf(90))
                .user(customer)
                .build();

        Bet bet2 = Bet.builder()
                .stake(BigDecimal.valueOf(30))
                .totalOdds(BigDecimal.valueOf(2.1))
                .winAmount(BigDecimal.valueOf(63))
                .user(customer)
                .build();

        betRepo.saveAll(java.util.List.of(bet1, bet2));

        // when
        Optional<java.util.List<Bet>> bets = betRepo.findAllByUserId(customer.getId());

        // then
        assertThat(bets).isPresent();
        assertThat(bets.get()).hasSize(2);
    }




}
