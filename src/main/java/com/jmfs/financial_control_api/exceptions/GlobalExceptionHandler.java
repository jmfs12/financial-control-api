package com.jmfs.financial_control_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail userNotFoundException(UserNotFoundException e){
        log.warn("User not found: {}", e.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("User Not Found");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCode", "USER_NOT_FOUND");
        return problemDetail;
    }

    @ExceptionHandler(WrongPasswordException.class)
    public ProblemDetail wrongPasswordException(WrongPasswordException e){
        log.warn("Wrong password received for: {}", e.getMessage()); 

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problemDetail.setTitle("Wrong Password");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCode", "WRONG_PASSWORD");
        return problemDetail;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail userAlreadyExistsException(UserAlreadyExistsException e){
        log.warn("User already exists: {}", e.getMessage()); 

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("User Already Exists");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCode", "USER_ALREADY_EXISTS");
        return problemDetail;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail accessDeniedException(AccessDeniedException e){
        log.warn("User don't have permission {}", e.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setTitle("Access Denied");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCode", "ACCESS_DENIED");
        return problemDetail;
    }
}
