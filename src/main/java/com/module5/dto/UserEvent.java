package com.module5.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEvent {

    private String email;
    private OperationType operationType;

    public enum OperationType {
        CREATED, DELETED
    }
}
