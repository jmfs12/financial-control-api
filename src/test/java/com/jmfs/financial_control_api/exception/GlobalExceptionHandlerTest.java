package com.jmfs.financial_control_api.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import com.jmfs.financial_control_api.exceptions.GlobalExceptionHandler;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;
import com.jmfs.financial_control_api.exceptions.UserAlreadyExistsException;
import com.jmfs.financial_control_api.exceptions.WrongPasswordException;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    
    @Test
    @DisplayName("Should return problem detail when user not found exception throw")
    void UserNotFoundException(){
        UserNotFoundException ex = new UserNotFoundException("User with ID 123 not found");

        ProblemDetail problem = globalExceptionHandler.userNotFoundException(ex);

        assertNotNull(problem);
        assertEquals(HttpStatus.NOT_FOUND.value(), problem.getStatus());
        assertEquals("User Not Found", problem.getTitle());
        assertEquals("User with ID 123 not found", problem.getDetail());
        assertEquals("USER_NOT_FOUND", problem.getProperties().get("errorCode"));
    }

    @Test
    @DisplayName("Should return problem detail when user already exists exception throw")
    void UserAlreadyExistsException(){
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User with ID 123 already exists");

        ProblemDetail problem = globalExceptionHandler.userAlreadyExistsException(ex);

        assertNotNull(problem);
        assertEquals(HttpStatus.CONFLICT.value(), problem.getStatus());
        assertEquals("User Already Exists", problem.getTitle());
        assertEquals("User with ID 123 already exists", problem.getDetail());
        assertEquals("USER_ALREADY_EXISTS", problem.getProperties().get("errorCode"));
    }

    @Test
    @DisplayName("Should return problem detail when user already exists exception throw")
    void WrongPasswordException(){
        WrongPasswordException ex = new WrongPasswordException("Wrong password received for user 123");

        ProblemDetail problem = globalExceptionHandler.wrongPasswordException(ex);

        assertNotNull(problem);
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problem.getStatus());
        assertEquals("Wrong Password", problem.getTitle());
        assertEquals("Wrong password received for user 123", problem.getDetail());
        assertEquals("WRONG_PASSWORD", problem.getProperties().get("errorCode"));
    }


    
}
