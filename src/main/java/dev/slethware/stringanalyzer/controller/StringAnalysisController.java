package dev.slethware.stringanalyzer.controller;

import dev.slethware.stringanalyzer.models.dto.StringAnalysisRequest;
import dev.slethware.stringanalyzer.models.dto.StringAnalysisResponse;
import dev.slethware.stringanalyzer.models.dto.StringListResponse;
import dev.slethware.stringanalyzer.service.StringAnalysisService;
import dev.slethware.stringanalyzer.utility.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/strings")
@RequiredArgsConstructor
@Tag(name = "String Analysis", description = "API for analyzing and managing strings")
public class StringAnalysisController {

    private final StringAnalysisService service;

    @PostMapping
    @Operation(summary = "Analyze and store a string")
    public ResponseEntity<?> analyzeString(@Valid @RequestBody StringAnalysisRequest request) {
        StringAnalysisResponse response = service.analyzeAndStore(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseUtil.successFullCreate("String analyzed successfully", response));
    }

    @GetMapping("/{value}")
    @Operation(summary = "Get string by value")
    public ResponseEntity<?> getStringByValue(@PathVariable String value) {
        StringAnalysisResponse response = service.getByValue(value);
        return ResponseEntity.ok(ApiResponseUtil.successFull("String retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all strings with optional filters")
    public ResponseEntity<?> getAllStrings(
            @RequestParam(required = false) Boolean is_palindrome,
            @RequestParam(required = false) Integer min_length,
            @RequestParam(required = false) Integer max_length,
            @RequestParam(required = false) Integer word_count,
            @RequestParam(required = false) String contains_character) {

        StringListResponse response = service.getAllWithFilters(
                is_palindrome, min_length, max_length, word_count, contains_character);
        return ResponseEntity.ok(ApiResponseUtil.successFull("Strings retrieved successfully", response));
    }
}