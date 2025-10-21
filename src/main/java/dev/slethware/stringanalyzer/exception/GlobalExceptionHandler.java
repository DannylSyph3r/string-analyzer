package dev.slethware.stringanalyzer.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> badRequestExceptionHandler(BadRequestException e) {
        log.error(e.getMessage(), e);
        Map<String, Object> body = new HashMap<>();
        body.put("message", e.getMessage());
        body.put("statusCode", e.getStatus().value());
        body.put("isSuccessful", false);
        return new ResponseEntity<>(body, e.getStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> resourceNotFoundExceptionHandler(ResourceNotFoundException e) {
        log.error(e.getMessage(), e);
        Map<String, Object> body = new HashMap<>();
        body.put("message", e.getMessage());
        body.put("statusCode", e.getStatus().value());
        body.put("isSuccessful", false);
        return new ResponseEntity<>(body, e.getStatus());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> conflictExceptionHandler(ConflictException e) {
        log.error(e.getMessage(), e);
        Map<String, Object> body = new HashMap<>();
        body.put("message", e.getMessage());
        body.put("statusCode", e.getStatus().value());
        body.put("isSuccessful", false);
        return new ResponseEntity<>(body, e.getStatus());
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<Map<String, Object>> unprocessableEntityExceptionHandler(UnprocessableEntityException e) {
        log.error(e.getMessage(), e);
        Map<String, Object> body = new HashMap<>();
        body.put("message", e.getMessage());
        body.put("statusCode", e.getStatus().value());
        body.put("isSuccessful", false);
        return new ResponseEntity<>(body, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Parameter conversion error: {}", ex.getMessage());

        String message = String.format("Invalid parameter '%s': expected %s but received '%s'",
                ex.getName(),
                ex.getRequiredType().getSimpleName(),
                ex.getValue());

        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("statusCode", HttpStatus.BAD_REQUEST.value());
        body.put("isSuccessful", false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {

        log.error(ex.getMessage(), ex);

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Invalid Arguments");
        body.put("statusCode", HttpStatus.BAD_REQUEST.value());
        body.put("isSuccessful", false);
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        log.error("No handler found: {}", ex.getRequestURL());

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Endpoint not found: " + ex.getRequestURL());
        body.put("statusCode", HttpStatus.NOT_FOUND.value());
        body.put("isSuccessful", false);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        log.error("Message not readable error: {}", ex.getMessage());

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormatEx) {
            for (JsonMappingException.Reference ref : invalidFormatEx.getPath()) {
                if ("value".equals(ref.getFieldName()) && invalidFormatEx.getTargetType() == String.class) {
                    Map<String, Object> body = new HashMap<>();
                    body.put("message", "Field must be a string");
                    body.put("statusCode", HttpStatus.UNPROCESSABLE_ENTITY.value());
                    body.put("isSuccessful", false);

                    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
                }
            }
        }

        Map<String, Object> body = new HashMap<>();
        body.put("message", "Invalid request body");
        body.put("statusCode", HttpStatus.BAD_REQUEST.value());
        body.put("isSuccessful", false);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {

        log.error(ex.getMessage(), ex);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", ex.getMessage());
        responseBody.put("statusCode", statusCode.value());
        responseBody.put("isSuccessful", false);

        return new ResponseEntity<>(responseBody, statusCode);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnpredictableException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        Map<String, Object> body = new HashMap<>();
        body.put("message", ex.getMessage());
        body.put("statusCode", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("isSuccessful", false);

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}