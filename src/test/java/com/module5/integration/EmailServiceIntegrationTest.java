package com.module5.integration;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetup;
import com.module5.dto.UserEvent;
import com.module5.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles({"test", "test-email"})
class EmailServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(
            new ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP)
    );

    @Autowired
    private EmailService emailService;

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.setUser("test@example.com", "test");
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void testSendEmailForUserCreation() throws Exception {
        UserEvent userEvent = UserEvent.builder()
                .email("test@example.com")
                .operationType(UserEvent.OperationType.CREATED)
                .build();

        emailService.sendEmail(userEvent);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Should receive exactly one email");

        MimeMessage message = messages[0];
        String content = message.getContent().toString();

        assertTrue(content.contains("Здравствуйте"));
        assertTrue(content.contains("аккаунт на сайте test-site.com"));
        assertTrue(content.contains("успешно создан"));
    }

    @Test
    void testSendEmailForUserDeletion() throws Exception {
        UserEvent userEvent = UserEvent.builder()
                .email("delete@example.com")
                .operationType(UserEvent.OperationType.DELETED)
                .build();

        emailService.sendEmail(userEvent);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Should receive exactly one email");

        MimeMessage message = messages[0];
        String content = message.getContent().toString();

        assertTrue(content.contains("Здравствуйте"));
        assertTrue(content.contains("аккаунт был удалён"));
    }
}
