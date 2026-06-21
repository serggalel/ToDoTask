package com.internship.todotask.task.exception.handler;

import com.internship.todotask.task.exception.UserCannotBeCollaboratorException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserCannotBeCollaboratorExceptionHandler {

    @ExceptionHandler(UserCannotBeCollaboratorException.class)
    public ResponseEntity<String> handleUserCannotBeCollaborator(UserCannotBeCollaboratorException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
