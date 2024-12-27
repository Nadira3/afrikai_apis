package com.precious.UserApi.model.user;

import com.precious.UserApi.model.enums.UserRole;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("ADMIN")
@NoArgsConstructor
public class Admin extends User {

    // Constructor for Admin-specific creation
    public Admin(String username, String email, String password, UserRole role) {
        super(username, email, password, role);
    }
}

