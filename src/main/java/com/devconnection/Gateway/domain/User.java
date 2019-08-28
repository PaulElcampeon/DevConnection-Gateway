package com.devconnection.Gateway.domain;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.devconnection.Gateway.messages.RegistrationMessage;

@Data
@Document(collection = "USERS")
public class User {

    @Id
    private String email;
    private String username;
    private String password;
    private List<String> roles = Arrays.asList("USER");

    public User() {
    }

    public User(RegistrationMessage registrationMessage) {
        this.username = registrationMessage.getUsername();
        this.password = registrationMessage.getPassword();
    }
}