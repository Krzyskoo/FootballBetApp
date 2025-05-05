package com.example.demo.services;

import com.example.demo.model.Customer;
import com.example.demo.model.Payment;
import com.example.demo.model.PaymentStatus;
import com.example.demo.model.TransactionType;
import com.example.demo.repo.CustomerRepo;
import com.example.demo.repo.PaymentRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final CustomerService customerService;
    private final CustomerRepo customerRepo;
    private final BalanceHistoryService balanceHistoryService;

    public PaymentService(PaymentRepo paymentRepo, CustomerService customerService, CustomerRepo customerRepo, BalanceHistoryService balanceHistoryService) {
        this.paymentRepo = paymentRepo;
        this.customerService = customerService;
        this.customerRepo = customerRepo;
        this.balanceHistoryService = balanceHistoryService;
    }
    @Transactional
    public void updatePaymentStatus(String paymentIdInDatabase, String paymentId, String stripeEventType) {
        Optional<Payment> paymentOpt = paymentRepo.findById(Long.parseLong(paymentIdInDatabase));
        if (paymentOpt.isPresent() &&
                paymentOpt.get().getStatus().toString().equals("PENDING")&&
                stripeEventType.equals("checkout.session.completed")) {
            Payment payment = paymentOpt.get();
            payment.setStripePaymentId(paymentId);
            paymentRepo.save(payment);
        } else if (paymentOpt.isPresent() &&
                paymentOpt.get().getStatus().toString().equals("PENDING")
                && stripeEventType.equals("payment_intent.succeeded")) {
            Payment payment = paymentOpt.get();
            payment.setStatus(PaymentStatus.SUCCEEDED);
            paymentRepo.save(payment);
            Customer customer = customerRepo
                    .findById(paymentOpt.get().getCustomer().getId())
                    .orElseThrow();
            balanceHistoryService.saveBalanceChange(customer, TransactionType.DEPOSIT, payment.getAmount(), "Deposit");

        }

    }

}
