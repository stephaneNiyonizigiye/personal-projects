package com.eventaccess.eventaccess.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtil {

    @Value("${JWT_SECRET}")
    private String secret;

    public String generateToken(String username) throws
            IllegalArgumentException, JWTCreationException {
        return JWT.create()
                .withSubject("User Details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("Event Access application")
                .sign(Algorithm.HMAC512(secret));
    }

    public String ValidateTokenAndRetrieveSubject(String token)throws
            JWTVerificationException {

        JWTVerifier  verifier = JWT.require(Algorithm.HMAC512(secret))
                .withIssuer("Event Access application")
                .withSubject("User Details")
                .build();
        DecodedJWT jwt=verifier.verify(token);
        return jwt.getClaim("username").asString();
    }
}
