package com.example.demo.services;

import com.example.demo.model.Customer;
import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.repo.PaymentRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {
    private final PaymentRepo paymentRepo;
    private final CustomerService customerService;
    private final PaymentService paymentService;

    public StripeService(PaymentRepo paymentRepo, CustomerService customerService, PaymentService paymentService) {
        this.paymentRepo = paymentRepo;
        this.customerService = customerService;
        this.paymentService = paymentService;
    }

    public String createCheckoutSession(long amount, String currency, String paymentId) throws StripeException {

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://your-website.com/success")
                .setCancelUrl("https://your-website.com/cancel")
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amount * 100)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Depozyt na saldo")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .putMetadata("payment_id", paymentId)
                .build();

        Session session = Session.create(params);


        return session.getUrl(); // Zwrot URL do przekierowania na Stripe
    }

    public String createCheckoutUrl(long amount, String currency) throws StripeException {
        Customer customer = customerService.findById(customerService.getAuthenticatedUsername());
        Payment newPayment =paymentRepo.save(
                Payment.builder()
                        .amount(BigDecimal.valueOf(amount))
                        .currency(currency)
                        .status(PaymentStatus.PENDING)
                        .customer(customer)
                        .customerEmail(customer.getEmail())
                        .paymentMethod("card")
                        .build()
        );
        String paymentId = newPayment.getId().toString();

       return createCheckoutSession(amount, currency,paymentId);
    }

    public ResponseEntity<String> handleWebhook(Event stripeEvent) throws StripeException {

        if (stripeEvent.getType().equals("checkout.session.completed")) {
            Session session = (Session) stripeEvent.getData().getObject();
            if (session == null) {
                return ResponseEntity.badRequest().body("No session data in event");
            }

            System.out.println("Session ID: " + session.getId());
            System.out.println("Session Metadata: " + session.getMetadata());

            // Pobranie metadanych i PaymentIntent ID
            String paymentIdInDatabase = session.getMetadata().get("payment_id");
            String paymentId = session.getPaymentIntent();

            if (paymentIdInDatabase == null || paymentId == null) {
                return ResponseEntity.badRequest().body("Missing metadata in session");
            }

            paymentService.finalUpdatePaymentStatus(paymentIdInDatabase, paymentId);
            return ResponseEntity.ok("OK");


        }
        return ResponseEntity.ok("OK");
    }

}
