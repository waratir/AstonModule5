package com.module5.service.impl;

import com.module5.dto.UserEvent;
import com.module5.service.EmailService;
import com.module5.service.UserEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumerImpl implements UserEventConsumer {

    private final EmailService emailService;

    @Override
    @KafkaListener(topics = "${app.kafka.topic.user-events:user-events}")
    public void consume(UserEvent userEvent) {
        log.info("Received user event from Kafka: operationType={}, email={}",
                userEvent.getOperationType(), userEvent.getEmail());

        emailService.sendEmail(userEvent);
        log.info("Successfully sent email to: {}", userEvent.getEmail());
    }
}