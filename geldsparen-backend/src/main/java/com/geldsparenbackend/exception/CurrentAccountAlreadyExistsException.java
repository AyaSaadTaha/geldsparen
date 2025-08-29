package com.geldsparenbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // error 409
public class CurrentAccountAlreadyExistsException extends RuntimeException {
    public CurrentAccountAlreadyExistsException() {
        super("Current account already exists for this user.");
    }
}