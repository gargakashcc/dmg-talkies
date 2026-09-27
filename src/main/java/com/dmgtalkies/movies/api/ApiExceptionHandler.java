package com.dmgtalkies.movies.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ErrorResponse> status(ResponseStatusException exception) { return ResponseEntity.status(exception.getStatusCode()).body(new ErrorResponse(exception.getReason())); }
    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    ResponseEntity<ErrorResponse> invalid(Exception exception) { return ResponseEntity.badRequest().body(new ErrorResponse("validation failed")); }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> other(Exception exception) { return ResponseEntity.internalServerError().body(new ErrorResponse("internal error")); }
}
