package com.example.demo.services;

import com.example.demo.model.Customer;
import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {
    PaymentService paymentService;

    public StripeService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public String createCheckoutSession(long amount, String currency, Customer customer) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://your-website.com/success")
                .setCancelUrl("https://your-website.com/cancel")
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD) // Tylko karta!
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
                .build();

        Session session = Session.create(params);
        Payment payment = Payment.builder()
                .stripePaymentId(session.getId())  // ID sesji Stripe
                .amount(BigDecimal.valueOf(amount))
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .customer(customer)  // Zakładając, że masz encję User jako Customer
                .customerEmail(customer.getEmail())
                .paymentMethod("card")
                .build();

        paymentService.savePayment(payment);
        return session.getUrl(); // Zwrot URL do przekierowania na Stripe
    }

}
