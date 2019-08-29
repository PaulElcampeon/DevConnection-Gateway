package com.devconnection.Gateway.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.devconnection.Gateway.controllers.RegistrationController;
import com.devconnection.Gateway.messages.RegistrationMessage;
import com.devconnection.Gateway.services.UserService;
import com.devconnection.Gateway.utils.RegistrationValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private RegistrationValidator registrationValidator;

    @MockBean
    private UserService userService;

    @MockBean
    @Qualifier("CustomUserDetailsService")
    private UserDetailsService userDetailsService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String email = "dave@live.co.uk";

    private String differentEmail = "jave@live.co.uk";

    private String incorrectEmail = "jave@livek";

    private String username = "user1";

    private String emptyUsername = "";

    private String differentUser = "user2";

    private String password = "pass12";

    private String differentPassword = "pass13";

    private String shortPassword = "pass";

    @Test
    public void registerAccount_valid() throws Exception {
        RegistrationMessage RegistrationMessage = new RegistrationMessage(username, username, password, password, email, email);

        attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage, HttpServletResponse.SC_CREATED, "created");

        verify(userService, times(1)).createUser(RegistrationMessage);
        VerifyEmailExistsAndValidateCalled(email);
    }

    @Test
    public void registerAccount_invalid_emptyFields() throws Exception {
        RegistrationMessage RegistrationMessage = new RegistrationMessage(emptyUsername, username, password, password, email, email);

        attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage, HttpServletResponse.SC_FORBIDDEN, "Cannot have blank fields");

        verify(registrationValidator, times(1)).validate(Mockito.any(RegistrationMessage.class), Mockito.any(HttpServletResponse.class));
    }

    @Test
    public void registerAccount_invalid_passwordsDoNotMatch() throws Exception {
        RegistrationMessage RegistrationMessage = new RegistrationMessage(username, username, password, differentPassword, email, email);

        attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage, HttpServletResponse.SC_FORBIDDEN, "Passwords do not match");

        VerifyEmailExistsAndValidateCalled(email);
    }

    @Test
    public void registerAccount_invalid_usernamesDoNotMatch() throws Exception {
        RegistrationMessage RegistrationMessage = new RegistrationMessage(username, differentUser, password, password, email, email);

        attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage, HttpServletResponse.SC_FORBIDDEN, "Usernames do not match");

        VerifyEmailExistsAndValidateCalled(email);
    }

    @Test
    public void registerAccount_invalid_emailsDoNotMatch() throws Exception {
        RegistrationMessage RegistrationMessage = new RegistrationMessage(username, username, password, password, email, differentEmail);

        attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage, HttpServletResponse.SC_FORBIDDEN, "Emails do not match");

        VerifyEmailExistsAndValidateCalled(email);
    }

    @Test
    public void registerAccount_invalid_passwordInvalid() throws Exception {
        RegistrationMessage RegistrationMessage = new RegistrationMessage(username, username, shortPassword, shortPassword, email, email);

        attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage, HttpServletResponse.SC_FORBIDDEN, "Password is not valid");

        VerifyEmailExistsAndValidateCalled(email);
    }

    @Test
    public void registerAccount_invalid_EmailAlreadyExists() throws Exception {
        RegistrationMessage RegistrationMessage = new RegistrationMessage(username, username, password, password, email, email);

        when(userService.checkIfUserWithEmailExists(RegistrationMessage.getEmail())).thenReturn(true);

        attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage, HttpServletResponse.SC_FORBIDDEN, "Email already exists");

        VerifyEmailExistsAndValidateCalled(email);
    }

    @Test
    public void registerAccount_invalid_EmailFormatWrong() throws Exception {
        RegistrationMessage RegistrationMessage = new RegistrationMessage(username, username, password, password, incorrectEmail, incorrectEmail);

        when(userService.checkIfUserWithEmailExists(RegistrationMessage.getEmail())).thenReturn(false);

        attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage, HttpServletResponse.SC_FORBIDDEN, "Email format is not correct");

        VerifyEmailExistsAndValidateCalled(incorrectEmail);
    }

    private void attemptAccountRegistrationWithExpectedStatusCodeAndMessage(RegistrationMessage RegistrationMessage, int httpStatusCode, String message) throws Exception {
        mockMvc.perform(post("/account/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RegistrationMessage)))
                .andExpect(status().is(httpStatusCode))
                .andExpect(status().reason(message));
    }

    private void VerifyEmailExistsAndValidateCalled(String email) throws IOException {
        verify(userService, times(1)).checkIfUserWithEmailExists(email);
        verify(registrationValidator, times(1)).validate(Mockito.any(RegistrationMessage.class), Mockito.any(HttpServletResponse.class));
    }

}
