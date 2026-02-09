package com.warehouse.exceptionHandler;

import com.warehouse.exceptions.InvalidRollDataException;
import com.warehouse.exceptions.RollNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(RollNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRollNotFound(RollNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ROLL_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(InvalidRollDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(InvalidRollDataException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_DATA", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR", "An internal error has occurred"));
    }

    public record ErrorResponse(String code, String message) {}
}
