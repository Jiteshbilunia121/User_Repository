package org.example.Event;


import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventProducer {



    private final KafkaTemplate<String, PaymentIntent> eventKafkaTemplate ;

    public PaymentEventProducer(KafkaTemplate<String, PaymentIntent> eventKafkaTemplate) {
        this.eventKafkaTemplate = eventKafkaTemplate;
    }

    public void sendPaymentStatus(PaymentIntent paymentIntent) {

        eventKafkaTemplate.send("parking.payment", paymentIntent);

    }

}
