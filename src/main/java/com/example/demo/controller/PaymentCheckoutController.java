package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.services.CustomerService;
import com.example.demo.services.StripeService;
import com.stripe.exception.StripeException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PaymentCheckoutController {

    private final StripeService stripeService;
    private final CustomerService customerService;

    public PaymentCheckoutController(StripeService stripeService, CustomerService customerService) {
        this.stripeService = stripeService;
        this.customerService = customerService;
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody Map<String, Object> request, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            long amount = ((Number) request.get("amount")).longValue();
            String currency = (String) request.get("currency");
            Customer customer = customerService.findById(customerService.getAuthenticatedUsername());

            String checkoutUrl = stripeService.createCheckoutSession(amount, currency,customer);
            return ResponseEntity.ok(Map.of("checkout_url", checkoutUrl));
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/index")
    public String index(){
        return "udało sie";
    }

    @GetMapping("/success")
    public String success(){
        return "success";
    }
}
