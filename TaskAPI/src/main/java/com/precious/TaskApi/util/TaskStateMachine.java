package com.precious.TaskApi.util;

import java.util.EnumMap;
import java.util.Set;

import com.precious.TaskApi.model.enums.TaskStatus;

public class TaskStateMachine {

    private static final EnumMap<TaskStatus, Set<TaskStatus>> allowedTransitions = new EnumMap<>(TaskStatus.class);

    static {
        allowedTransitions.put(TaskStatus.PENDING, Set.of(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED));
        allowedTransitions.put(TaskStatus.IN_PROGRESS, Set.of(TaskStatus.COMPLETED, TaskStatus.CANCELLED));
        allowedTransitions.put(TaskStatus.COMPLETED, Set.of()); // No transitions from completed
        allowedTransitions.put(TaskStatus.CANCELLED, Set.of()); // No transitions from canceled
    }

    public static boolean isTransitionValid(TaskStatus from, TaskStatus to) {
        Set<TaskStatus> validTransitions = allowedTransitions.get(from);
        return validTransitions != null && validTransitions.contains(to);
    }
}
