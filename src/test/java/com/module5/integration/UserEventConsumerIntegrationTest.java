package com.module5.integration;

import com.module5.dto.UserEvent;
import com.module5.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@ActiveProfiles({"test", "test-kafka"})
@Slf4j
class UserEventConsumerIntegrationTest {

    @Container
    @ServiceConnection
    static final KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
    );

    @MockitoBean
    private EmailService emailService;

    @Autowired
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Test
    void shouldConsumeUserCreatedEvent() throws Exception {
        UserEvent event = UserEvent.builder()
                .email("integration-test@example.com")
                .operationType(UserEvent.OperationType.CREATED)
                .build();

        kafkaTemplate.send("user-events", event.getEmail(), event).get(5, TimeUnit.SECONDS);
        log.info("Message sent to Kafka: {}", event);

        await()
                .atMost(Duration.ofSeconds(10))
                .pollDelay(Duration.ofSeconds(1))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    verify(emailService, times(1)).sendEmail(argThat(receivedEvent ->
                            receivedEvent.getEmail().equals("integration-test@example.com") &&
                                    receivedEvent.getOperationType() == UserEvent.OperationType.CREATED
                    ));
                    log.info("EmailService was called successfully");
                });
    }

    @Test
    void shouldConsumeUserDeletedEvent() throws Exception {
        UserEvent event = UserEvent.builder()
                .email("delete-test@example.com")
                .operationType(UserEvent.OperationType.DELETED)
                .build();

        kafkaTemplate.send("user-events", event.getEmail(), event).get(5, TimeUnit.SECONDS);
        log.info("Message sent to Kafka: {}", event);

        await()
                .atMost(Duration.ofSeconds(10))
                .pollDelay(Duration.ofSeconds(1))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() -> {
                    verify(emailService, times(1)).sendEmail(argThat(receivedEvent ->
                            receivedEvent.getEmail().equals("delete-test@example.com") &&
                                    receivedEvent.getOperationType() == UserEvent.OperationType.DELETED
                    ));
                });
    }
}
