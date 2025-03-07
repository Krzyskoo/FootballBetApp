package com.example.demo.services;

import com.example.demo.proxy.SportApiProxy;
import org.springframework.stereotype.Service;

@Service
public class BetSelectionService {

   private final SportApiProxy proxy;

    public BetSelectionService(SportApiProxy proxy) {
        this.proxy = proxy;
    }
}
