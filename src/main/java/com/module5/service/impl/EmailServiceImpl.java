package com.module5.service.impl;

import com.module5.dto.UserEvent;
import com.module5.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.site-name:наш сайт}")
    private String siteName;

    @Override
    public void sendEmail(UserEvent userEvent) {
        String to = userEvent.getEmail();
        UserEvent.OperationType operationType = userEvent.getOperationType();

        if (to == null || to.isBlank()) {
            log.error("Email address is null or empty");
            throw new IllegalArgumentException("Email address cannot be null or empty");
        }

        if (operationType == null) {
            log.error("Operation type is null for email: {}", to);
            throw new IllegalArgumentException("Operation type cannot be null");
        }

        log.info("Preparing to send email to: {} for operation: {}", to, operationType);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(getSubject(operationType));
            message.setText(getEmailText(operationType));

            mailSender.send(message);
            log.info("Email sent successfully to: {} for operation: {}", to, operationType);

        } catch (Exception e) {
            log.error("Failed to send email to: {} for operation: {}", to, operationType, e);
            throw new RuntimeException("Failed to send email to: " + to, e);
        }
    }

    private String getSubject(UserEvent.OperationType operationType) {
        return operationType == UserEvent.OperationType.CREATED ?
                "Аккаунт создан" : "Аккаунт удалён";
    }

    private String getEmailText(UserEvent.OperationType operationType) {
        if (operationType == UserEvent.OperationType.CREATED) {
            return String.format("Здравствуйте! Ваш аккаунт на сайте %s был успешно создан.", siteName);
        } else {
            return "Здравствуйте! Ваш аккаунт был удалён.";
        }
    }
}
