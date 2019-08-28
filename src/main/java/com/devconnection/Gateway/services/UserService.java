package com.devconnection.Gateway.services;

import com.devconnection.Gateway.domain.User;
import com.devconnection.Gateway.messages.RegistrationMessage;

public interface UserService {

    User getUser(String email);

    void createUser(RegistrationMessage registrationMessage);

    boolean checkIfUserWithEmailExists(String email);

    void deleteAll();
}
