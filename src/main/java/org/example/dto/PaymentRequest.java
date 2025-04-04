package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long userId;
    private String vehicleNumber;
    private double amount;
    private String paymentMethodId;


    public boolean processPayment() {

        // Payment logic (mocking success for now)
        return true;
    }

}
