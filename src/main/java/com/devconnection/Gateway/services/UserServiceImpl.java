package com.devconnection.Gateway.services;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.devconnection.Gateway.domain.User;
import com.devconnection.Gateway.messages.RegistrationMessage;
import com.devconnection.Gateway.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(String username) {
        return userRepository.findById(username).orElseThrow(NoSuchElementException::new);
    }

    public void createUser(RegistrationMessage registrationMessage) {
        registrationMessage.setPassword(passwordEncoder.encode(registrationMessage.getPassword()));
        userRepository.insert(new User(registrationMessage));
    }

    public boolean checkIfUserWithUsernameExists(String username) {
        return userRepository.existsById(username);
    }

    public void deleteAll() {
        userRepository.deleteAll();
    }
}
