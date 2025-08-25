package com.jmfs.financial_control_api.exception;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import com.jmfs.financial_control_api.exceptions.GlobalExceptionHandler;
import com.jmfs.financial_control_api.exceptions.UserNotFoundException;

public class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    
    @Test
    @DisplayName("Should return problem detail when user not found exception throw")
    void UserNotFoundException(){
        UserNotFoundException ex = new UserNotFoundException("User with ID 123 not found");

        ProblemDetail problem = globalExceptionHandler.exception(ex);

        assertNotNull(problem);
        assertEquals(HttpStatus.NOT_FOUND.value(), problem.getStatus());
        assertEquals("User Not Found", problem.getTitle());
        assertEquals("User with ID 123 not found", problem.getDetail());
        assertEquals("USER_NOT_FOUND", problem.getProperties().get("errorCode"));
    }
}
