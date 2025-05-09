package com.example.demo.services;

import com.example.demo.model.Customer;
import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.repo.PaymentRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Slf4j
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
        log.info("Creating checkout session for amount: {}, currency: {}", amount, currency);
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:4200/payment-success")
                .setCancelUrl("http://localhost:4200/payment-cancel")
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
                .setPaymentIntentData(
                        SessionCreateParams.PaymentIntentData.builder()
                                .putMetadata("payment_id", paymentId)
                                .build())
                .build();

        Session session = Session.create(params);

        return session.getUrl();
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

    public ResponseEntity<String> handleWebhook(Event stripeEvent) {
        log.info("Received webhook event: {}", stripeEvent.getType());

        String eventType = stripeEvent.getType();

        if ("checkout.session.completed".equals(eventType)) {
            return handleCheckoutSessionCompleted(stripeEvent);
        } else if ("payment_intent.succeeded".equals(eventType)) {
            return handlePaymentIntentSucceeded(stripeEvent);
        }

        return ResponseEntity.ok("OK");
    }

    private ResponseEntity<String> handleCheckoutSessionCompleted(Event stripeEvent) {

        Session session = (Session) stripeEvent.getData().getObject();
        if (session == null) {
            return ResponseEntity.badRequest().body("No session data in event");
        }

        String paymentIdInDatabase = session.getMetadata().get("payment_id");
        String paymentId = session.getPaymentIntent();

        log.info("Payment ID in database: {}, Payment ID: {}", paymentIdInDatabase, paymentId);




        if (paymentIdInDatabase == null || paymentId == null) {
            return ResponseEntity.badRequest().body("Missing metadata in session");
        }

        paymentService.updatePaymentStatus(paymentIdInDatabase, paymentId, "checkout.session.completed");

        return ResponseEntity.ok("OK");
    }

    private ResponseEntity<String> handlePaymentIntentSucceeded(Event stripeEvent) {
        log.info("Received payment_intent.succeeded event");
        PaymentIntent paymentIntent = (PaymentIntent) stripeEvent.getData().getObject();
        if (paymentIntent == null) {
            return ResponseEntity.badRequest().body("No payment intent data in event");
        }

        log.info("Payment ID: {}", paymentIntent.getId());

        String paymentIdInDatabase = paymentIntent.getMetadata().get("payment_id");

        if (paymentIdInDatabase == null) {
            log.error("Missing payment_id in metadata");
            return ResponseEntity.badRequest().body("Missing payment_id in metadata");
        }

        paymentService.updatePaymentStatus(paymentIdInDatabase, paymentIntent.getId(), "payment_intent.succeeded");
        log.info("Payment intent succeeded for payment ID: {}", paymentIdInDatabase);

        return ResponseEntity.ok("OK");
    }
}
