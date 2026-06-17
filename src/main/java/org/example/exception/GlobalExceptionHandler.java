package org.example.exception;

import org.example.security.exception.UserFoundException;
import org.example.security.exception.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            UserFoundException.class,
            UserNotFoundException.class
    })
    ResponseEntity<Object> handleConflictInternalServer(Exception ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        return super.handleExceptionInternal(ex, Map.of("message", bodyOfResponse),
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
