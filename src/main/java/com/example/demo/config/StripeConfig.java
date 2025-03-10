package com.example.demo.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @PostConstruct
    public void init(){
        Stripe.apiKey="sk_test_51OHCJtDC7PH0QkQkgOZfQKT0MLhcPVSyTOX1lRLeeDwPpCDHmXmSVvyTWlW45jS1xN5gWBjfibWS6zr2RJdlEoTJ00nxzxvhYK";
    }
}
