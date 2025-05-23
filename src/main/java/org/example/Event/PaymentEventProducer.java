package org.example.Event;


import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
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

    public void sendPaymentStatus(Session session, PaymentRequest paymentRequest) {
//        System.out.println("User Email :"+ paymentIntent.getReceiptEmail());

        PaymentInfoDTO dto = new PaymentInfoDTO();
        Optional<User> user = userRepository.findById(paymentRequest.getUserId());

        System.out.println("User Email :"+ user.get().getEmail());
        System.out.println("amount: " + session.getAmountTotal());
        System.out.println("currency: " + session.getCurrency());
        System.out.println("TransactionId: " + session.getId());


        dto.setEmail(user.get().getEmail());
        dto.setUserId(paymentRequest.getUserId());
        dto.setAmount(session.getAmountTotal());
        dto.setCurrency(session.getCurrency());
        dto.setTransactionId(session.getId());


        eventKafkaTemplate.send("parking.payment", dto);

    }

}
