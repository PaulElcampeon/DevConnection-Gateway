package com.devconnection.Gateway.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.devconnection.Gateway.messages.CreateProfile;
import com.devconnection.Gateway.messages.GenericMessage;
import com.devconnection.Gateway.messages.RegistrationMessage;
import com.devconnection.Gateway.services.UserService;
import com.devconnection.Gateway.utils.RegistrationValidator;

@RestController
public class RegistrationController {

    @Autowired
    private RegistrationValidator registrationValidator;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private final String profileService = "profile-service";

    private final String postBoxService = "postbox-service";

    @RequestMapping(value = "/account/register", method = RequestMethod.POST)
    public void registerAccount(HttpServletResponse httpServletResponse, @RequestBody RegistrationMessage registrationMessage) throws IOException {
        if (registrationValidator.validate(registrationMessage, httpServletResponse)) {
            userService.createUser(registrationMessage);
            createProfile(registrationMessage.getEmail(), registrationMessage.getUsername());
            createPostBox(registrationMessage.getEmail());
        }
    }

    private void createProfile(String email, String username) {

        restTemplate.postForEntity("http://"+profileService+"/profile-service/create", new CreateProfile(email, username), String.class);
    }

    private void createPostBox(String email) {
        restTemplate.postForEntity("http://"+postBoxService+"/postbox-service/create", new GenericMessage(email), String.class);
    }
}
