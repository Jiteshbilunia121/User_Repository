package org.example.controller;


import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
//import com.stripe.model.billingportal.Session;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;


import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
//import com.stripe.param.billingportal.SessionCreateParams;
import org.example.Event.PaymentEventProducer;
import org.example.UserService.UserPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.example.Entity.User;
import org.example.UserService.UserService;
import org.example.dto.LoginRequest;
import org.example.dto.PaymentRequest;
import org.example.dto.PaymentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;
import org.example.util.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    private final UserService userService;

    @Autowired
    private final UserPaymentService userPaymentService;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Autowired
    private PaymentEventProducer paymentEventProducer;

    public UserController(PasswordEncoder passwordEncoder, JwtUtil jwtUtil, UserService userService, UserPaymentService userPaymentService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userPaymentService = userPaymentService;
    }


    // User Registration API

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        if (userService.getUserByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already registered!");
        }
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // Get User by Email API
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
        Optional<User> user = userService.getUserByEmail(email);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("user email : " + loginRequest.getUserEmail() + " " + "user password: " + loginRequest.getUserPassword());
        Optional<User> userOptional = userService.getUserByEmail(loginRequest.getUserEmail());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getUserPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail());

                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("userId", String.valueOf(user.getId()));
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.badRequest().body("Invalid email or password");
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest paymentRequest) {


        try {

            Stripe.apiKey = stripeApiKey;
            boolean paymentSuccess = paymentRequest.processPayment();

            // Create PaymentIntent in Stripe
            PaymentIntent paymentIntent = userPaymentService.makePayment(paymentRequest);

            // Check the payment status
            if ("succeeded".equals(paymentIntent.getStatus())) {
                PaymentResponse paymentResponse = new PaymentResponse(true, "Payment successful");
                log.info("Transaction ID: {}", paymentResponse.getTransactionId());
                return ResponseEntity.ok(paymentResponse);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new PaymentResponse(false, "Payment requires action: " + paymentIntent.getStatus()));
            }

        }
        catch (StripeException e) {
            log.error("Stripe Payment error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PaymentResponse(false, "Stripe error: " + e.getMessage()));
        }


    }

//    @PostMapping("/create-payment-intent")
//    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) {
//        try {
//            PaymentIntent paymentIntent = userPaymentService.createPaymentIntent(paymentRequest);
//            return ResponseEntity.ok().body(
//                    new HashMap<String, String>() {{
//                        put("clientSecret", paymentIntent.getClientSecret());
//                    }}
//            );
//        } catch (StripeException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new PaymentResponse(false, "Payment initialization failed"));
//        }
//    }


    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody PaymentRequest paymentRequest) {
        try {
            // Build the line item and session parameters
           Session session = userPaymentService.sessionCreateParams(paymentRequest);
            // Create the checkout session

            // Return session ID to frontend
            Map<String, String> responseData = new HashMap<>();
            responseData.put("sessionId", session.getId());

            return ResponseEntity.ok(responseData);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PaymentResponse(false, "Checkout session creation failed"));
        }
    }


    @GetMapping("/check-session")
    public ResponseEntity<?> checkSession(@RequestParam String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            if ("complete".equals(session.getStatus())) {
                return ResponseEntity.ok(Map.of("message", "Payment successful"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Session not complete"));
            }
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error fetching session"));
        }
    }

}
