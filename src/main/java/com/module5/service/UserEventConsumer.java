package com.module5.service;

import com.module5.dto.UserEvent;

public interface UserEventConsumer {
    void consume(UserEvent userEvent);
}
