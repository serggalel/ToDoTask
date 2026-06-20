package com.internship.todotask.task.exception.handler;

import com.internship.todotask.task.exception.UserIsNotTheOwnerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserIsNotTheOwnerExceptionHandler {

    @ExceptionHandler(UserIsNotTheOwnerException.class)
    public ResponseEntity<String> handleUserIsNotTheOwner(UserIsNotTheOwnerException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
