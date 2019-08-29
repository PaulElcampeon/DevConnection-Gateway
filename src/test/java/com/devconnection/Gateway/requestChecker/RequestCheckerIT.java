package com.devconnection.Gateway.requestChecker;

import com.devconnection.Gateway.GatewayApplication;
import com.devconnection.Gateway.config.SecurityConfig;
import com.devconnection.Gateway.messages.GenericMessage;
import com.devconnection.Gateway.messages.LoginMessage;
import com.devconnection.Gateway.messages.RegistrationMessage;
import com.devconnection.Gateway.repositories.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

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

    private String wrongEmail = "jave@live.com";

    private String username = "dave";

    private String password = "pass12";

    @Before
    public void init() {
        testRestTemplate.postForEntity("http://localhost:" + port + "/account/register", new RegistrationMessage(username, username, password, password, email, email), String.class);
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/login", new LoginMessage(email, password), String.class);
        jwtToken = responseEntity.getHeaders().get("Authorization").get(0);
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void projectService_create_403() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/project-service/create", new GenericMessage(wrongEmail), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void projectService_delete_403() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/project-service/delete", new GenericMessage(wrongEmail), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void projectService_modify_403() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/project-service/modify", new GenericMessage(wrongEmail), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void projectService_update_403() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/project-service/update/description", new GenericMessage(wrongEmail), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void postboxService_create_500() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/postbox-service/create", new GenericMessage(email), String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void profileService_update_403() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/profile-service/update", new GenericMessage(wrongEmail), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void profileService_create_500() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/profile-service/create", new GenericMessage(email), String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    public void postboxService_delete_403() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/postbox-service/delete", new GenericMessage(email), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    public void postboxService_get_403() {
        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity("http://localhost:" + port + "/postbox-service/get", new GenericMessage(email), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }
}
