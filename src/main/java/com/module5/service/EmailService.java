package com.module5.service;

import com.module5.dto.UserEvent;


public interface EmailService {

    /**
     * Sends email based on user events
     * @param userEvent Event with information about the user and the type of operation
     * @throws RuntimeException if an error occurred while sending
     */
    void sendEmail(UserEvent userEvent);
}
