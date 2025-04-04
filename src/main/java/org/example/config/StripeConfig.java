package org.example.config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.key}") // Inject the secret key from properties
    private String stripeSecretKey;

    @Bean
    public String setStripeApiKey() {
        Stripe.apiKey = stripeSecretKey;
        return stripeSecretKey; // ✅ Must return a value (String in this case)
    }
}
