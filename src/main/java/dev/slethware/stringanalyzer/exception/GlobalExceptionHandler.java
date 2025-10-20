package dev.slethware.stringanalyzer.exception;

import dev.slethware.stringanalyzer.models.ApiResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> badRequestExceptionHandler(BadRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), e.getStatus().value(), false, null), e.getStatus());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> resourceNotFoundExceptionHandler(ResourceNotFoundException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ApiResponse<>(e.getMessage(), e.getStatus().value(), false, null), e.getStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Parameter conversion error: {}", ex.getMessage());

        String message = String.format("Invalid parameter '%s': expected %s but received '%s'",
                ex.getName(),
                ex.getRequiredType().getSimpleName(),
                ex.getValue());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(message, HttpStatus.BAD_REQUEST.value(), false, null));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {

        log.error(ex.getMessage(), ex);

        Map<String, String> data = new HashMap<>();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            data.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        var response = ApiResponse.builder()
                .data(data)
                .message("Invalid Arguments")
                .statusCode(status.value())
                .isSuccessful(false)
                .build();

        return new ResponseEntity<>(response, status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode statusCode,
            @NonNull WebRequest request) {

        log.error(ex.getMessage(), ex);
        var response = ApiResponse.builder()
                .message(ex.getMessage())
                .statusCode(statusCode.value())
                .isSuccessful(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnpredictableException(Exception ex) {
        log.error(ex.getMessage(), ex);
        var response = ApiResponse.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .isSuccessful(false)
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}