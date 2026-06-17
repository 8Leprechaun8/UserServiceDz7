package org.example.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserMessageProducer.class);

    @Autowired
    public KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(Object message) {
        kafkaTemplate.send("users", message);
        logger.info("Sent: " + message);
    }
}
