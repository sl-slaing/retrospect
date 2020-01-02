package com.example.retrospect.web.managers;

import com.example.retrospect.core.exceptions.NotFoundException;
import com.example.retrospect.core.exceptions.NotPermittedException;
import com.example.retrospect.core.exceptions.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ValidationException.class })
    protected ResponseEntity<Object> handleValidationError(ValidationException exc, WebRequest request) {
        return handleExceptionInternal(exc, exc.getMessage(), new HttpHeaders(), HttpStatus.PRECONDITION_FAILED, request);
    }

    @ExceptionHandler(value = {NotFoundException.class })
    protected ResponseEntity<Object> handleNotFoundError(NotFoundException exc, WebRequest request) {
        return handleExceptionInternal(exc, exc.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {NotPermittedException.class })
    protected ResponseEntity<Object> handleNotPermitted(NotPermittedException exc, WebRequest request) {
        return handleExceptionInternal(exc, exc.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {Exception.class })
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException exc, WebRequest request) {
        return handleExceptionInternal(exc, exc.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
