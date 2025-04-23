package com.example.demo.controller;

import com.example.demo.repo.PaymentRepo;
import com.example.demo.services.CustomerService;
import com.example.demo.services.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PaymentCheckoutController {

    private final StripeService stripeService;
    private final CustomerService customerService;
    private final PaymentRepo paymentRepo;

    public PaymentCheckoutController(StripeService stripeService, CustomerService customerService, PaymentRepo paymentRepo) {
        this.stripeService = stripeService;
        this.customerService = customerService;
        this.paymentRepo = paymentRepo;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> request) throws StripeException {
            long amount = ((Number) request.get("amount")).longValue();
            String currency = (String) request.get("currency");
            String checkoutUrl = stripeService.createCheckoutUrl(amount, currency);
            return ResponseEntity.ok(Map.of("checkout_url", checkoutUrl));
    }
}
