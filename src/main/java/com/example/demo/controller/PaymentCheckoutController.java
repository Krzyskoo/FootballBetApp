package com.example.demo.controller;

import com.example.demo.dtos.PaymentIntentRequestDTO;
import com.example.demo.repo.PaymentRepo;
import com.example.demo.services.CustomerService;
import com.example.demo.services.StripeService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@Slf4j
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
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary     = "Create Stripe Checkout session",
            description = "Initializes a Stripe payment session for the specified amount and currency, and returns the Checkout URL."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description  = "URL to the Stripe Checkout session",
                    content      = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(
                                    type    = "string",
                                    example = "https://checkout.stripe.com/pay/cs_test_123456"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description  = "Invalid input data",
                    content      = @Content(schema = @Schema())
            ),
            @ApiResponse(
                    responseCode = "500",
                    description  = "Error connecting to Stripe or internal server error",
                    content      = @Content(schema = @Schema())
            )
    })
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @Valid @RequestBody PaymentIntentRequestDTO paymentIntentRequest
    ) throws StripeException {
        log.info("Creating Stripe Checkout session for amount={} {}",
                paymentIntentRequest.getAmount(),
                paymentIntentRequest.getCurrency());
        String checkoutUrl = stripeService.createCheckoutUrl(
                paymentIntentRequest.getAmount(),
                paymentIntentRequest.getCurrency()
        );
        return ResponseEntity.ok(Map.of("checkout_url", checkoutUrl));
    }

}
