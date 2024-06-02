package com.mortypoul.education.exceptions;


import com.mortypoul.education.configuration.MyUserDetails;
import com.mortypoul.education.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String REASON_STATUS = "reason_status";
    private static final String REASON_PHRASE = "reason_phrase";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String PATH = "path";
    private static final String USERNAME = "username";

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof MyUserDetails)) {
            throw new ResourceNotFoundException("logged in user");
        }
        return ((MyUserDetails) principal).getUser();
    }

    private Map<String, Object> getFixedErrorBody(WebRequest request, HttpStatus httpStatus) {
        Map<String, Object> body = new HashMap<>();
        body.put(TIMESTAMP, System.currentTimeMillis());
        body.put(PATH, request.getDescription(false));
        body.put(USERNAME, getCurrentUser().getUsername());
        body.put(REASON_STATUS, httpStatus.value());
        body.put(REASON_PHRASE, httpStatus.getReasonPhrase());
        return body;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handler(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> body = getFixedErrorBody(request, HttpStatus.NOT_FOUND);
        body.put(ERROR, "Not Found!");
        body.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handler(CustomException ex, WebRequest request) {
        Map<String, Object> body = getFixedErrorBody(request, ex.getHttpStatus());
        body.put(ERROR, ex.getMessage());
        body.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handler(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, Object> body = getFixedErrorBody(request, HttpStatus.NOT_FOUND);
        // body.put(error, ex.getMessage()); // to avoid show long technical issue
        body.put(MESSAGE, "Validation failed!");
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> body.put("validate field [" + e.getField() + "]", e.getDefaultMessage()));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handler(AuthenticationException ex, WebRequest request) {
        Map<String, Object> body = getFixedErrorBody(request, HttpStatus.NOT_FOUND);
        body.put(ERROR, ex.getMessage());
        body.put(MESSAGE, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handler(Exception ex, WebRequest request) {
        String exceptionMessage = ex.getMessage();
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String generalError = "Internal Server Error";

        if (exceptionMessage.contains("could not execute statement")) {
            if (exceptionMessage.contains("Referential integrity constraint violation")) {
                exceptionMessage = "The record is in use and cannot be deleted!";
                httpStatus = HttpStatus.CONFLICT;
                generalError = "Internal data conflict happened!";
            }
        } else if (exceptionMessage.contains("Access is denied") ||
                (exceptionMessage.toLowerCase().contains("access") && exceptionMessage.toLowerCase().contains("denied"))) {
            exceptionMessage = "Access is denied!";
            httpStatus = HttpStatus.UNAUTHORIZED;
            generalError = "Unauthorized!";
        }

        Map<String, Object> body = getFixedErrorBody(request, httpStatus);
        body.put(ERROR, generalError);
        body.put(MESSAGE, exceptionMessage);

        ex.printStackTrace();
        return new ResponseEntity<>(body, httpStatus);
    }

}
