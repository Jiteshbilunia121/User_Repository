package org.example.Event;


import com.stripe.model.PaymentIntent;
import org.example.Entity.User;
import org.example.dto.PaymentInfoDTO;
import org.example.dto.PaymentRequest;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentEventProducer {



    private final KafkaTemplate<String, PaymentInfoDTO> eventKafkaTemplate ;

    @Autowired
    private UserRepository userRepository;

    public PaymentEventProducer(KafkaTemplate<String, PaymentInfoDTO> eventKafkaTemplate) {
        this.eventKafkaTemplate = eventKafkaTemplate;
    }

    public void sendPaymentStatus(PaymentIntent paymentIntent, PaymentRequest paymentRequest) {
//        System.out.println("User Email :"+ paymentIntent.getReceiptEmail());

        PaymentInfoDTO dto = new PaymentInfoDTO();
        Optional<User> user = userRepository.findById(paymentRequest.getUserId());

        System.out.println("User Email :"+ user.get().getEmail());
        System.out.println("amount: " + paymentIntent.getAmount());
        System.out.println("currency: " + paymentIntent.getCurrency());
        System.out.println("TransactionId: " + paymentIntent.getId());


        dto.setEmail(user.get().getEmail());
        dto.setUserId(paymentRequest.getUserId());
        dto.setAmount(paymentIntent.getAmount());
        dto.setCurrency(paymentIntent.getCurrency());
        dto.setTransactionId(paymentIntent.getId());


        eventKafkaTemplate.send("parking.payment", dto);

    }

}
