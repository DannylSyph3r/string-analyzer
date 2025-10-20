package dev.slethware.stringanalyzer.utility;

import dev.slethware.stringanalyzer.models.ApiResponse;
import org.springframework.http.HttpStatus;

public class ApiResponseUtil {

    public static <T> ApiResponse<T> successFull(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .statusCode(HttpStatus.OK.value())
                .isSuccessful(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> successFullCreate(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .statusCode(HttpStatus.CREATED.value())
                .isSuccessful(true)
                .data(data)
                .build();
    }
}