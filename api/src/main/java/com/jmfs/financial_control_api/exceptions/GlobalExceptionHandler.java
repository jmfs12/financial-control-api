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
    public ProblemDetail exception(UserNotFoundException e){
         log.warn("User not found: {}", e.getMessage()); // log at WARN, no stacktrace needed

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("User Not Found");
        problemDetail.setDetail(e.getMessage());
        problemDetail.setProperty("errorCode", "USER_NOT_FOUND");
        return problemDetail;
    }
}
