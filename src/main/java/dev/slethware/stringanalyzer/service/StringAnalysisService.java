package dev.slethware.stringanalyzer.service;

import dev.slethware.stringanalyzer.models.dto.StringAnalysisRequest;
import dev.slethware.stringanalyzer.models.dto.StringAnalysisResponse;
import dev.slethware.stringanalyzer.models.dto.StringListResponse;

public interface StringAnalysisService {

    StringAnalysisResponse analyzeAndStore(StringAnalysisRequest request);
    StringAnalysisResponse getByValue(String value);
    StringListResponse getAllWithFilters(Boolean isPalindrome, Integer minLength, Integer maxLength, Integer wordCount, String containsCharacter);
}