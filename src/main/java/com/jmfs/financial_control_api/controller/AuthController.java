package com.jmfs.financial_control_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.jmfs.financial_control_api.dto.AuthRequest;
import com.jmfs.financial_control_api.dto.AuthResponse;
import com.jmfs.financial_control_api.service.impl.AuthServiceImpl;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest){
        AuthResponse authResponse = authService.login(authRequest);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest authRequest){
        AuthResponse authResponse = authService.register(authRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authResponse);
    }
}
