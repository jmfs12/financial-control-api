package com.jmfs.financial_control_api.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.service.spec.ITokenService;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TokenService implements ITokenService {
    @Value("${api.security.token.secret}")
    private String SECRET;

    @Override
    public String generateToken(User user){
        log.debug("[TOKEN SERVICE] Generating token for user: {}", user.getId());
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        String token = JWT.create()
                .withIssuer("financial-control")
                .withClaim("id", user.getId())
                .withSubject(user.getEmail())
                .withExpiresAt(getExpirationTime())
                .sign(algorithm);

        log.debug("[TOKEN SERVICE] Token generated succesfully for user: {}", user.getId());
        return token;
    }

    @Override
    public String validateToken(String token){
        try{
            log.debug("[TOKEN SERVICE] Validating token");
    
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.require(algorithm)
                    .withIssuer("financial-control")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e){
            log.warn("[TOKEN SERVICE] Invalid token", e);
            return null;
        }
    }

    @Override
    public Long extractUserId(String token){
        log.debug("[TOKEN SERVICE] Extracting id from token");
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(token);

        Long id = jwt.getClaim("id").asLong();
        log.debug("[TOKEN SERVICE] Id extracted: {}", id);

        return id;
    }

    private Instant getExpirationTime() {
        return LocalDateTime.now()
                .plusHours(2)
                .toInstant(ZoneOffset.of("-3"));
    }
}
