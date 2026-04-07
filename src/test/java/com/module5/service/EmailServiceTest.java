package com.module5.service;

import com.module5.dto.UserEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@ActiveProfiles("test")
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    void testSendEmailForUserCreation() {
        UserEvent userEvent = UserEvent.builder()
                .email("test@example.com")
                .operationType(UserEvent.OperationType.CREATED)
                .build();

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(userEvent);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("test@example.com", sentMessage.getTo()[0]);
        assertEquals("Аккаунт создан", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("аккаунт на сайте"));
        assertTrue(sentMessage.getText().contains("успешно создан"));
    }

    @Test
    void testSendEmailForUserDeletion() {
        UserEvent userEvent = UserEvent.builder()
                .email("delete@example.com")
                .operationType(UserEvent.OperationType.DELETED)
                .build();

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(userEvent);

        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("delete@example.com", sentMessage.getTo()[0]);
        assertEquals("Аккаунт удалён", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("аккаунт был удалён"));
    }

    @Test
    void testSendMultipleEmails() {
        UserEvent userEvent1 = UserEvent.builder()
                .email("user1@example.com")
                .operationType(UserEvent.OperationType.CREATED)
                .build();

        UserEvent userEvent2 = UserEvent.builder()
                .email("user2@example.com")
                .operationType(UserEvent.OperationType.DELETED)
                .build();

        emailService.sendEmail(userEvent1);
        emailService.sendEmail(userEvent2);

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testEmailContentForCreation() {
        UserEvent userEvent = UserEvent.builder()
                .email("test@example.com")
                .operationType(UserEvent.OperationType.CREATED)
                .build();

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(userEvent);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        String text = message.getText();

        assertTrue(text.contains("Здравствуйте"));
        assertTrue(text.contains("аккаунт на сайте"));
        assertTrue(text.contains("успешно создан"));
        assertFalse(text.contains("удалён"));
    }

    @Test
    void testEmailContentForDeletion() {
        UserEvent userEvent = UserEvent.builder()
                .email("test@example.com")
                .operationType(UserEvent.OperationType.DELETED)
                .build();

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        emailService.sendEmail(userEvent);

        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        String text = message.getText();

        assertTrue(text.contains("Здравствуйте"));
        assertTrue(text.contains("аккаунт был удалён"));
        assertFalse(text.contains("создан"));
    }
}
