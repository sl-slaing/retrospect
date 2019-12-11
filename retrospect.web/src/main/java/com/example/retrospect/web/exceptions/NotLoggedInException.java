package com.example.retrospect.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class NotLoggedInException extends RuntimeException {
    public NotLoggedInException(String message) {
        super(message);
    }
}
