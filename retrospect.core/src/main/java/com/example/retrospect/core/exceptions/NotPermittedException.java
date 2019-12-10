package com.example.retrospect.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NotPermittedException extends RuntimeException {
    public NotPermittedException(String message) {
        super(message);
    }
}
