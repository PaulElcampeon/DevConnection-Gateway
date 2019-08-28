package com.devconnection.Gateway.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationMessage {

    private String username;
    private String confirmUsername;
    private String password;
    private String confirmPassword;

}
