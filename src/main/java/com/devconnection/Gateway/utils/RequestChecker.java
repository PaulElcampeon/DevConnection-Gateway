package com.devconnection.Gateway.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.devconnection.Gateway.messages.GenericMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestChecker {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static boolean isRequestOK(HttpServletRequest request, UsernamePasswordAuthenticationToken decodedToken) {
        String path = request.getServletPath();
        GenericMessage genericMessage;
        String email = null;
        try {
            genericMessage = objectMapper.readValue(request.getInputStream(), GenericMessage.class);
            email = genericMessage.getEmail();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (path.contains("project-service")) {

            return isRequestToProjectServiceOk(request, decodedToken, email);

        } else if (path.contains("profile-service")) {

            return isRequestToProfileServiceOk(request, decodedToken, email);

        } else {//"postbox-service"

            return isRequestToPostboxServiceOk(request, decodedToken, email);
        }
    }

    private static boolean isRequestToProjectServiceOk(HttpServletRequest request, UsernamePasswordAuthenticationToken decodedToken, String email) {
        String path = request.getServletPath();
        if (path.contains("create") || path.contains("delete") || path.contains("modify") || path.contains("description")) {
            return (decodedToken.getName().equalsIgnoreCase(email));
        }
        return true;
    }

    private static boolean isRequestToProfileServiceOk(HttpServletRequest request, UsernamePasswordAuthenticationToken decodedToken, String email) {
        String path = request.getServletPath();
        if (path.contains("update")) {
            return (decodedToken.getName().equalsIgnoreCase(email));
        }
        return true;
    }

    private static boolean isRequestToPostboxServiceOk(HttpServletRequest request, UsernamePasswordAuthenticationToken decodedToken, String email) {
        String path = request.getServletPath();
        if (path.contains("get") || path.contains("delete")) {
            return (decodedToken.getName().equalsIgnoreCase(email));
        }
        return true;
    }
}
