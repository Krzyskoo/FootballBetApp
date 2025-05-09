package com.example.demo.controller;

import com.example.demo.services.SportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class SportController {
    private final SportService sportService;

    public SportController(SportService sportService) {
        this.sportService = sportService;
    }

    @GetMapping("/sports")
    public ResponseEntity<Set<Object>> getMessagesByStatus(){
        return ResponseEntity.status(HttpStatus.OK).body(sportService.getSports());
    }
}
