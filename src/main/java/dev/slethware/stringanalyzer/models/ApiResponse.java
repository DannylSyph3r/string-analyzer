package dev.slethware.stringanalyzer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private int statusCode;
    private boolean isSuccessful;
    private T data;
}