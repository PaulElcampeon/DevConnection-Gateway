package com.devconnection.Gateway.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.devconnection.Gateway.messages.RegistrationMessage;
import com.devconnection.Gateway.services.UserService;
import com.devconnection.Gateway.utils.RegistrationValidator;

@RestController
public class RegistrationController {

    @Autowired
    private RegistrationValidator registrationValidator;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/account/register", method = RequestMethod.POST)
    public void registerAccount(HttpServletResponse httpServletResponse, @RequestBody RegistrationMessage registrationMessage) throws IOException {
        if (registrationValidator.validate(registrationMessage, httpServletResponse)) {
            userService.createUser(registrationMessage);
        }
    }
}
