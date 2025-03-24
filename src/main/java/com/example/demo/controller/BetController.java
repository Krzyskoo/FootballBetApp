package com.example.demo.controller;

import com.example.demo.Dtos.BetRequest;
import com.example.demo.model.Bet;
import com.example.demo.services.BetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BetController {

    private final BetService betService;

    @PostMapping("/bets")
    public ResponseEntity<Bet> placeBet(@Validated @RequestBody BetRequest betRequest) {
        Bet createdBet = betService.createBet(betRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBet);

    }
}
