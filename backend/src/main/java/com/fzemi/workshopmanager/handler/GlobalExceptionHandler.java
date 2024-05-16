package com.fzemi.workshopmanager.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // TODO: Add auth exceptions


    /**
     * Handles request validation errors
     *
     * @param exc validation exception
     * @return response with validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException exc) {
        Set<String> errors = new HashSet<>();
        exc.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity.status(BAD_REQUEST).body(
                ExceptionResponse.builder()
                        .errorCode(BAD_REQUEST.value())
                        .validationErrors(errors)
                        .build()
        );
    }

    /**
     * Handles data integrity violation exceptions (ex. unique constraint violation)
     *
     * @param exc data integrity violation exception
     * @return response with error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleException(DataIntegrityViolationException exc) {
        String errorMessage = exc.getMostSpecificCause().getMessage();

        Pattern pattern = Pattern.compile("PUBLIC\\.\\w+\\sON\\sPUBLIC\\.(\\w+)\\((\\w+)\\sNULLS\\sFIRST\\)");
        Matcher matcher = pattern.matcher(errorMessage);

        if (matcher.find()) {
            // Extract the class name and field name from the error message
            String className = matcher.group(1);
            String fieldName = matcher.group(2);

            return ResponseEntity.status(BAD_REQUEST).body(
                    ExceptionResponse.builder()
                            .errorCode(BAD_REQUEST.value())
                            .errorDescription("A " + className + " with this " + fieldName + " already exists")
                            .build()
            );
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ExceptionResponse.builder()
                        .errorDescription("Internal server error")
                        .error(exc.getMessage())
                        .build()
        );
    }

    /**
     * Handles custom exceptions thrown by the application in business logic
     *
     * @param exc custom exception
     * @return response with error message
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ExceptionResponse> handleException(AppException exc) {
        ErrorCodes errorCode = exc.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(
                ExceptionResponse.builder()
                        .errorCode(errorCode.getCode())
                        .errorDescription(errorCode.getDescription())
                        .error(exc.getMessage())
                        .build()
        );
    }

    /**
     * Handles all other exceptions
     *
     * @param exc exception
     * @return response with error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception exc) {
        exc.printStackTrace();
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
                ExceptionResponse.builder()
                        .errorCode(INTERNAL_SERVER_ERROR.value())
                        .errorDescription("Internal server error")
                        .error(exc.getMessage())
                        .build()
        );
    }
}
