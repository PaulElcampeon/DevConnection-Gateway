package com.devconnection.Gateway.requestChecker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.devconnection.Gateway.GatewayApplication;
import com.devconnection.Gateway.config.SecurityConfig;
import com.devconnection.Gateway.messages.LoginMessage;
import com.devconnection.Gateway.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(classes = {GatewayApplication.class, SecurityConfig.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
public class RequestCheckerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String jwtToken;

    private String email = "dave@live.com";

    private String username = "dave";

    private String password = "pass12";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void init() {
        testRestTemplate.postForEntity("")
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/login", objectMapper.writeValueAsString(new LoginMessage(email, password)), String.class);

    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void projectService_403() {



    }


}
