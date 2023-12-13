package com.example.todorest.exception;

import com.example.todorest.dto.RestDtoError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {ServiceImplNotFundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(Exception ex, WebRequest request) {
        RestDtoError restErrorDto = RestDtoError.builder()
                .statusCod(HttpStatus.NOT_FOUND.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, restErrorDto, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {ServiceImplConflictException.class})
    public ResponseEntity<Object> handleResourceConflictFoundException(Exception ex, WebRequest request) {
        RestDtoError restErrorDto = RestDtoError.builder()
                .statusCod(HttpStatus.CONFLICT.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, restErrorDto, new HttpHeaders(), HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(value = {ImageProcessingException.class})
    public ResponseEntity<Object> handleImageProcessingException(Exception ex, WebRequest request) {
        RestDtoError restErrorDto = RestDtoError.builder()
                .statusCod(HttpStatus.BAD_REQUEST.value())
                .errorMessage(ex.getMessage())
                .build();
        return handleExceptionInternal(ex, restErrorDto, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}

