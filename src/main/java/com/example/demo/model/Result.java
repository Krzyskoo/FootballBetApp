package com.example.demo.model;

import io.swagger.v3.oas.annotations.Hidden;

public enum Result {
    HOME_WIN,
    AWAY_WIN,
    DRAW,
    @Hidden
    PENDING,
    @Hidden
    WIN,
    @Hidden
    LOST
}
