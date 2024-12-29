package com.precious.TaskApi.model.enums;

import lombok.Getter;

@Getter
public enum NotificationPriority {
    LOW("LOW", 0),
    NORMAL("NORMAL", 1),
    HIGH("HIGH", 2),
    URGENT("URGENT", 3);

    private final String value;
    private final int level;

    NotificationPriority(String value, int level) {
        this.value = value;
        this.level = level;
    }
}
