package org.example.config;

import com.stripe.Stripe;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Bean
    public void setStripeApiKey() {
        // Set your secret key here from Stripe dashboard
        Stripe.apiKey = "sk_test_51R9N5TQVPTavWRF8SmYSp0EpfMn9hU8HRMgZaXcHtexYkaNh4CoL8UTzVFpfjNojaH63nSx0k9qEGBmKaeQ6JhSU00ft56T2KH";  // Replace with your own test secret key
    }
}
