package com.devanmejia.appmanager.configuration.handlers;

import com.devanmejia.appmanager.exception.EmailException;
import com.devanmejia.appmanager.exception.EntityException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({EmailException.class, EntityException.class})
    public ResponseEntity<ExceptionMessage> resourceNotFoundException(Exception exception) {
        var message = new ExceptionMessage(exception.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionMessage> validationException(MethodArgumentNotValidException exception) {
        var message = exception.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Request body is invalid");
        var body = new ExceptionMessage(message, HttpStatus.UNPROCESSABLE_ENTITY.value());
        return new ResponseEntity<>(body, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
