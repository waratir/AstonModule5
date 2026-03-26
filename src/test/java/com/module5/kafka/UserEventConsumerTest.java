package com.module5.kafka;


import com.module5.dto.UserEvent;
import com.module5.service.impl.EmailServiceImpl;
import com.module5.service.impl.UserEventConsumerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserEventConsumerTest {

    @Mock
    private EmailServiceImpl emailService;

    @InjectMocks
    private UserEventConsumerImpl userEventConsumer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "siteName", "test-site.com");
        ReflectionTestUtils.setField(emailService, "fromEmail", "noreply@test.com");
    }

    @Test
    void shouldProcessUserCreatedEvent() {
        UserEvent event = UserEvent.builder()
                .email("test@example.com")
                .operationType(UserEvent.OperationType.CREATED)
                .build();

        userEventConsumer.consume(event);

        ArgumentCaptor<UserEvent> captor = ArgumentCaptor.forClass(UserEvent.class);
        verify(emailService, times(1)).sendEmail(captor.capture());

        UserEvent capturedEvent = captor.getValue();
        assertEquals("test@example.com", capturedEvent.getEmail());
        assertEquals(UserEvent.OperationType.CREATED, capturedEvent.getOperationType());
    }

    @Test
    void shouldProcessUserDeletedEvent() {
        UserEvent event = UserEvent.builder()
                .email("delete@example.com")
                .operationType(UserEvent.OperationType.DELETED)
                .build();

        userEventConsumer.consume(event);

        verify(emailService, times(1)).sendEmail(event);
    }

    @Test
    void shouldHandleMultipleEvents() {
        UserEvent event1 = UserEvent.builder()
                .email("user1@example.com")
                .operationType(UserEvent.OperationType.CREATED)
                .build();

        UserEvent event2 = UserEvent.builder()
                .email("user2@example.com")
                .operationType(UserEvent.OperationType.DELETED)
                .build();

        userEventConsumer.consume(event1);
        userEventConsumer.consume(event2);

        verify(emailService, times(1)).sendEmail(event1);
        verify(emailService, times(1)).sendEmail(event2);
    }

    @Test
    void shouldPropagateExceptionWhenEmailServiceFails() {
        UserEvent event = UserEvent.builder()
                .email("test@example.com")
                .operationType(UserEvent.OperationType.CREATED)
                .build();

        doThrow(new RuntimeException("Email service failed")).when(emailService).sendEmail(any());

        assertThrows(RuntimeException.class, () -> userEventConsumer.consume(event));
        verify(emailService, times(1)).sendEmail(event);
    }
}
