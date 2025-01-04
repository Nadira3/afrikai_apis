package com.precious.UserApi.model.enums;

// Enum for User Roles
public enum UserRole {
    CLIENT, TASKER, ADMIN;

    public static boolean exists(String roleName) {
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }
}

