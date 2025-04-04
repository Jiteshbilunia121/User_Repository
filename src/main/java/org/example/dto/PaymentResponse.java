package org.example.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PaymentResponse {

    private String transactionId;
    private boolean success;
    private String message;

    // Constructor
    public PaymentResponse(boolean success, String message) {
        this.transactionId = UUID.randomUUID().toString(); // Generate unique transaction ID
        this.success = success;
        this.message = message;
    }

    // Getters & Setters
}

