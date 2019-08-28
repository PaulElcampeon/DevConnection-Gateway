package com.devconnection.Gateway.services;

import com.devconnection.Gateway.domain.User;
import com.devconnection.Gateway.messages.RegistrationMessage;

public interface UserService {

    User getUser(String username);

    void createUser(RegistrationMessage registrationMessage);

    boolean checkIfUserWithUsernameExists(String username);

    void deleteAll();
}
