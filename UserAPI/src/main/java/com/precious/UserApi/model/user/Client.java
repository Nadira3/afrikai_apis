package com.precious.UserApi.model.user;

import com.precious.UserApi.model.enums.UserRole;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;



@Entity
@DiscriminatorValue("CLIENT")
@NoArgsConstructor
public class Client extends User {
    // Constructor
    public Client(String username, String email, String password, UserRole role) {
        super(username, email, password, role);
    }

}

