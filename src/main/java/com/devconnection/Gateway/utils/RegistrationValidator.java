package com.devconnection.Gateway.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.devconnection.Gateway.messages.RegistrationMessage;
import com.devconnection.Gateway.services.UserService;

@Component
public class RegistrationValidator {

    private UserService userService;

    @Autowired
    public RegistrationValidator(UserService userService) {
        this.userService = userService;
    }

    public boolean validate(RegistrationMessage registrationMessage, HttpServletResponse response) throws IOException {
        if (checkEmptyFields(registrationMessage)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Cannot have blank fields");
        } else if (checkIfUsernameExists(registrationMessage.getUsername())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Username already exists");
        } else if (checkIfFieldsAreNotEqual(registrationMessage.getPassword(), registrationMessage.getConfirmPassword())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Passwords do not match");
        } else if (checkIfFieldsAreNotEqual(registrationMessage.getUsername(), registrationMessage.getConfirmUsername())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Usernames do not match");
        } else if (checkIfFieldsAreNotEqual(registrationMessage.getEmail(), registrationMessage.getConfirmEmail())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Emails do not match");
        } else if (checkIfEmailIncludesKeyCharacters(registrationMessage.getEmail())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Email format is not correct");
        } else if (checkPasswordIsInValid(registrationMessage.getPassword())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Password is not valid");
        } else {
            response.sendError(HttpServletResponse.SC_CREATED, "created");
        }
        return response.getStatus() == HttpServletResponse.SC_CREATED;
    }

    private boolean checkEmptyFields(RegistrationMessage registrationMessage) {
        return (
                registrationMessage.getUsername().length() == 0 ||
                        registrationMessage.getConfirmUsername().length() == 0 ||
                        registrationMessage.getPassword().length() == 0 ||
                        registrationMessage.getConfirmPassword().length() == 0
        );
    }

    private boolean checkIfUsernameExists(String username) {
        return userService.checkIfUserWithUsernameExists(username);
    }

    private boolean checkIfEmailIncludesKeyCharacters(String email) {
        return email.contains("@") && (StringUtils.countOccurrencesOf(email, ".") > 1);
    }

    private boolean checkIfFieldsAreNotEqual(String field1, String field2) {
        return !field1.equals(field2);
    }

    private boolean checkPasswordIsInValid(String password) {
        return password.length() < 5;
    }
}
