package com.example.demo.controller;

import com.example.demo.Dtos.PaymentIntentRequestDTO;
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
import org.springframework.http.MediaType;
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

    @Operation(
            summary     = "Utwórz sesję płatności Stripe",
            description = "Inicjuje sesję płatności w Stripe na podaną kwotę i walutę, zwraca URL Checkout"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description  = "Link do sesji Checkout Stripe",
                    content= @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema    = @Schema(type = "string",
                                    example = "https://checkout.stripe.com/pay/cs_test_123456")
                    )
            ),
            @ApiResponse(responseCode = "400", description  = "Nieprawidłowe dane wejściowe",
                    content= @Content(schema = @Schema())
            ),
            @ApiResponse(responseCode = "500", description  = "Błąd połączenia z Stripe lub inny błąd serwera",
                    content      = @Content(schema = @Schema())
            )
    })
    @SecurityRequirement(name = "JWT")
    @PostMapping("/create-payment-intent")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @RequestBody @Valid PaymentIntentRequestDTO paymentIntentRequest) throws StripeException {

            long amount = paymentIntentRequest.getAmount();
            String currency = paymentIntentRequest.getCurrency();
            String checkoutUrl = stripeService.createCheckoutUrl(amount, currency);
            return ResponseEntity.ok(Map.of("checkout_url", checkoutUrl));
    }
}
