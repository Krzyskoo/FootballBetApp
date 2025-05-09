package com.example.demo.services;

import com.example.demo.proxy.SportApiProxy;
import com.example.demo.repo.EventRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class SportService {

    private final SportApiProxy sportApiProxy;
    private final EventRepo eventRepo;
    private final Environment env;
    public Set<Object> getSports() {
        return eventRepo.getUniqueSportKeyAndSportTitle();
    }
}
