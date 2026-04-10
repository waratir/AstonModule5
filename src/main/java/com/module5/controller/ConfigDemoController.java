package com.module5.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class ConfigDemoController {

    @Value("${app.notification.welcome-message:Welcome to Notification Service}")
    private String welcomeMessage;

    @Value("${app.notification.email-retry-enabled:true}")
    private boolean emailRetryEnabled;

    @GetMapping("/api/config/demo")
    public String showConfig() {
        return String.format(
                "Welcome message: %s, Email retry enabled: %s",
                welcomeMessage,
                emailRetryEnabled
        );
    }
}
