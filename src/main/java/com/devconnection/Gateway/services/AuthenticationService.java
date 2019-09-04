package com.devconnection.Gateway.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.devconnection.Gateway.utils.RequestChecker;

public class AuthenticationService {

    public static final long EXPIRATIONTIME = 864_000_00; // 1 day in milliseconds
    public static final String SIGNINGKEY = "SecretKey";
    public static final String PREFIX = "Bearer";

    // Get token from Authorization header
    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(SIGNINGKEY)
                    .parseClaimsJws(token.replace(PREFIX, ""))
                    .getBody();

            String user = claims.getSubject();
            List<String> authorities = ((List<String>) claims.get("role"));
            List<GrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            if (user != null) {
                UsernamePasswordAuthenticationToken decodedToken = new UsernamePasswordAuthenticationToken(user, null,
                        grantedAuthorities);

//                if (request.getMethod().equalsIgnoreCase("POST") && !request.getServletPath().contains("login") && !request.getServletPath().contains("register")) {
//                    return RequestChecker.isRequestOK(request, decodedToken)? decodedToken : null;
//                }
                return decodedToken;
            }
        }
        return null;
    }

    // Add token to Authorization header
    public static void addToken(HttpServletResponse res, Authentication authentication) {
        List<String> authorities = authentication.getAuthorities().stream().map(role -> role.toString()).collect(Collectors.toList());
        Claims claims = Jwts.claims().setSubject(authentication.getName());
        claims.put("role", authorities);

        String JwtToken = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, SIGNINGKEY)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .compact();

        res.addHeader("Authorization", PREFIX + " " + JwtToken);
        res.addHeader("Access-Control-Expose-Headers", "Authorization");
    }
}
