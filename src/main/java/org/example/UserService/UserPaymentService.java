package org.example.UserService;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.transaction.Transactional;
import org.example.Event.PaymentEventProducer;
import org.example.dto.PaymentRequest;
import org.example.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
public class UserPaymentService {


    @Autowired
    private final PaymentEventProducer paymentEventProducer;

    public UserPaymentService(PaymentEventProducer paymentEventProducer) {
        this.paymentEventProducer = paymentEventProducer;
    }


    public PaymentIntent makePayment(PaymentRequest paymentRequest) throws StripeException {
            // Create PaymentIntent in Stripe
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount((long) (paymentRequest.getAmount() * 100L)) // Convert to cents
                    .setCurrency("usd") // Change currency if needed
                    .setPaymentMethod(paymentRequest.getPaymentMethodId()) // Payment method ID from frontend
                    .setConfirm(true) // Directly confirm the payment
                    .setReturnUrl("http://localhost:8080/payment-success") // Needed for some payment methods
                    .setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()).build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
//
//            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//                @Override
//                public void afterCommit() {
//
////                    String eventMessage = "User made the payment";
//                    paymentEventProducer.sendPaymentStatus(paymentIntent);
//                }
//            });
        paymentEventProducer.sendPaymentStatus(paymentIntent, paymentRequest);

        return paymentIntent;

    }
}

