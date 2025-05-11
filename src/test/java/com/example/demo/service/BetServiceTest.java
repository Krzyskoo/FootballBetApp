package com.example.demo.service;

import com.example.demo.dtos.BetRequest;
import com.example.demo.dtos.BetSelectionRequest;
import com.example.demo.exceptions.EventAlreadyStartedException;
import com.example.demo.exceptions.EventNotFoundException;
import com.example.demo.exceptions.InsufficientFundsException;
import com.example.demo.mapper.BetMapper;
import com.example.demo.model.*;
import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.BalanceHistoryRepo;
import com.example.demo.repo.BetRepo;
import com.example.demo.repo.CustomerRepo;
import com.example.demo.repo.EventRepo;
import com.example.demo.services.BalanceHistoryService;
import com.example.demo.services.BetService;
import com.example.demo.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BetServiceTest {
    @Mock
    private CustomerRepo customerRepo;
    @Mock
    private CustomerService customerService;
    @Mock
    private BalanceHistoryRepo balanceHistoryRepo;
    @Mock
    private BalanceHistoryService balanceHistoryService;
    @Mock
    private EventRepo eventRepo;
    @Mock
    private BetRepo betRepo;
    @Mock
    private SportApiProxy proxy;
    @Mock
    private Environment env;
    @Mock
    private BetMapper betMapper;

    @InjectMocks
    private BetService betService;

    @Test
    void shouldCreateBetSuccessfully() {
        Long userId = 1L;
        String eventId = "eventId";
        BigDecimal odds = new BigDecimal("3.50");
        BigDecimal stake = new BigDecimal("10");
        BigDecimal expectedTotalOdds = odds;
        BigDecimal expectedWinAmount = stake.multiply(odds);

        BetSelectionRequest betSelectionRequest = createBetSelectionRequest(eventId,Result.HOME_WIN);

        BetRequest betRequest = createBetRequest(stake,betSelectionRequest);

        Customer customer = Customer.builder()
                .id(userId)
                .email("email@wp.pl")
                .balance(new BigDecimal("200"))
                .build();

        Event event = Event.builder()
                .eventId(eventId)
                .startTime(new Date(System.currentTimeMillis() + 100000))
                .homeTeamOdds("3.50")
                .completed(false)
                .build();

        when(customerService.getAuthenticatedUsername()).thenReturn(userId);
        when(customerRepo.findById(userId)).thenReturn(Optional.of(customer));
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));
        when(betRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(balanceHistoryService).saveBalanceChange(any(), any(), any(), any());


        Bet result = betService.createBet(betRequest);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals(expectedTotalOdds, result.getTotalOdds());
        assertEquals(expectedWinAmount, result.getWinAmount());
        assertEquals(1, result.getSelections().size());
        assertEquals(customer.getId(), result.getUser().getId());

        verify(betRepo).save(any());
        verify(balanceHistoryService).saveBalanceChange(
                eq(customer),
                eq(TransactionType.BET_PLACED),
                eq(betRequest.getAmount()),
                eq("Bet placed")
        );


    }
    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {
        Long userId = 1L;
        Customer customer = Customer.builder()
                .id(userId)
                .email("email@wp.pl")
                .balance(new BigDecimal("10"))
                .build();
        BetSelectionRequest betSelectionRequest = createBetSelectionRequest("EVT2",Result.HOME_WIN);

        BetRequest betRequest = createBetRequest(new BigDecimal("100"),betSelectionRequest);

        when(customerService.getAuthenticatedUsername()).thenReturn(userId);
        when(customerRepo.findById(userId)).thenReturn(Optional.of(customer));

        assertThrows(InsufficientFundsException.class, () -> betService.createBet(betRequest));
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        Long userId = 1L;
        String eventId = "INVALID_EVT";
        Customer customer = Customer.builder()
                .id(userId)
                .balance(new BigDecimal("500"))
                .build();
        BetSelectionRequest betSelectionRequest = createBetSelectionRequest(eventId,Result.DRAW);
        BetRequest betRequest = createBetRequest(new BigDecimal("100"),betSelectionRequest);

        when(customerService.getAuthenticatedUsername()).thenReturn(userId);
        when(customerRepo.findById(userId)).thenReturn(Optional.of(customer));
        when(eventRepo.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> betService.createBet(betRequest));
    }
    @Test
    void shouldThrowWhenEventAlreadyStarted() {
        Long userId = 1L;
        String eventId = "EVT1";
        Customer customer = Customer.builder()
                .id(userId)
                .balance(new BigDecimal("500"))
                .build()
        ;
        Event event = Event.builder()
                .eventId(eventId)
                .startTime(new Date(System.currentTimeMillis() - 100000))
                .homeTeamOdds("3.50")
                .completed(false)
                .build();

        BetSelectionRequest betSelectionRequest = createBetSelectionRequest(eventId,Result.HOME_WIN);
        BetRequest betRequest = createBetRequest(new BigDecimal("50"),betSelectionRequest);

        when(customerService.getAuthenticatedUsername()).thenReturn(userId);
        when(customerRepo.findById(userId)).thenReturn(Optional.of(customer));
        when(eventRepo.findById(eventId)).thenReturn(Optional.of(event));

        assertThrows(EventAlreadyStartedException.class, () -> betService.createBet(betRequest));
    }

    public BetSelectionRequest createBetSelectionRequest(String eventId, Result predictedResult){
     return new BetSelectionRequest(eventId,predictedResult);
    }


    public BetRequest createBetRequest(BigDecimal amount, BetSelectionRequest betSelectionRequest){
        return new BetRequest(amount, List.of(betSelectionRequest));
    }

}
