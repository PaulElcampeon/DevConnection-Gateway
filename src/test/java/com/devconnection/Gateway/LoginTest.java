package com.devconnection.Gateway;

import com.devconnection.Gateway.config.SecurityConfig;
import com.devconnection.Gateway.messages.LoginMessage;
import com.devconnection.Gateway.messages.RegistrationMessage;
import com.devconnection.Gateway.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@SpringBootTest(classes = {GatewayApplication.class, SecurityConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String email = "dave@live.com";

    private String wrongEmail = "kate@live.com";

    private String username = "dave";

    private String password = "pass12";

    private String wrongPassword = "pass13";

    @Before
    public void init() {
        userService.createUser(new RegistrationMessage(username, username, password, password, email, email));
    }

    @After
    public void tearDown() {
        userService.deleteAll();
    }

    @Test
    public void login_success() throws Exception {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/login", objectMapper.writeValueAsString(new LoginMessage(email, password)), String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getHeaders().containsKey("Authorization"));
    }

    @Test
    public void login_wrongPassword() throws Exception {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/login", objectMapper.writeValueAsString(new LoginMessage(wrongEmail, password)), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void login_emailDoesntExist() throws Exception {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/login", objectMapper.writeValueAsString(new LoginMessage(email, wrongPassword)), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }
}