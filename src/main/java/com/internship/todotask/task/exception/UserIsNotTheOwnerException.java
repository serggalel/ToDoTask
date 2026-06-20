package com.internship.todotask.task.exception;

public class UserIsNotTheOwnerException extends RuntimeException {

    public UserIsNotTheOwnerException(String message) {
        super(message);
    }

}
