package com.example.demo.config;

import com.example.demo.constants.ApplicationConstants;
import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
@Configuration
public class StripeConfig {

    private final Environment env;
    @PostConstruct
    public void init(){
        Stripe.apiKey= env.getProperty(ApplicationConstants.STRIPE_API_KEY);
    }
}
