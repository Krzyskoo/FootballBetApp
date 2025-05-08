package com.example.demo.controller;

import com.example.demo.repo.PaymentRepo;
import com.example.demo.services.CustomerService;
import com.example.demo.services.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
public class PaymentCheckoutControllerTest {

    private MockMvc mockMvc;

    private StripeService stripeService;
    private CustomerService customerService;
    private PaymentRepo paymentRepo;

    @BeforeEach
    void setUp() {
        stripeService   = mock(StripeService.class);
        customerService = mock(CustomerService.class);
        paymentRepo     = mock(PaymentRepo.class);
        var controller = new PaymentCheckoutController(stripeService, customerService, paymentRepo);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("POST /create-payment-intent → 200 + checkout_url w body")
    void createPaymentIntent_success_thenReturnsUrl() throws Exception {
        // given
        long   amount   = 2000L;
        String currency = "usd";
        String fakeUrl  = "https://checkout.stripe.com/pay/session123";

        when(stripeService.createCheckoutUrl(amount, currency)).thenReturn(fakeUrl);

        String jsonRequest = """
            {
              "amount": 2000,
              "currency": "usd"
            }
            """;

        // when + then
        mockMvc.perform(post("/create-payment-intent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.checkout_url").value(fakeUrl));

        verify(stripeService).createCheckoutUrl(amount, currency);
    }

}
