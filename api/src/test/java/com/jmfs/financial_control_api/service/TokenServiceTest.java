package com.jmfs.financial_control_api.service;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jmfs.financial_control_api.entity.User;
import com.jmfs.financial_control_api.service.impl.TokenService;

public class TokenServiceTest {

	private TokenService tokenService;

	private User user;

	@BeforeEach
	void setUp() {
		tokenService = new TokenService();
		Field secretField;
		try {
			secretField = tokenService.getClass().getDeclaredField("SECRET");
			secretField.setAccessible(true);
			secretField.set(tokenService, "secret");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		user = new User();
		user.setId(1L);
		user.setEmail("test@example.com");
	}

	@Test
    @DisplayName("Should generate token succesfully")
	void testGenerateToken() {
		String token = tokenService.generateToken(user);
		Assertions.assertNotNull(token);
	}

	@Test
    @DisplayName("Should validate token succesfully")
	void testValidateToken() {
		String token = tokenService.generateToken(user);
		String subject = tokenService.validateToken(token);
		Assertions.assertEquals(user.getEmail(), subject);
	}

	@Test
    @DisplayName("Should return null when token is invalid")
	void testValidateToken_invalid() {
		String invalidToken = "invalid.token.value";
		String subject = tokenService.validateToken(invalidToken);
		Assertions.assertNull(subject);
	}

	@Test
    @DisplayName("Should extract user id succesfully")
	void testExtractUserId() {
		String token = tokenService.generateToken(user);
		Long id = tokenService.extractUserId(token);
		Assertions.assertEquals(user.getId(), id);
	}
}
