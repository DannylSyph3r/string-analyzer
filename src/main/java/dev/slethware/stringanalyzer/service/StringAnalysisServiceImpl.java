package dev.slethware.stringanalyzer.service;

import dev.slethware.stringanalyzer.exception.ConflictException;
import dev.slethware.stringanalyzer.models.dto.StringAnalysisRequest;
import dev.slethware.stringanalyzer.models.dto.StringAnalysisResponse;
import dev.slethware.stringanalyzer.models.dto.StringListResponse;
import dev.slethware.stringanalyzer.models.entity.Strings;
import dev.slethware.stringanalyzer.exception.BadRequestException;
import dev.slethware.stringanalyzer.exception.ResourceNotFoundException;
import dev.slethware.stringanalyzer.repository.StringsRepository;
import dev.slethware.stringanalyzer.utility.StringAnalyzerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StringAnalysisServiceImpl implements StringAnalysisService {

    private final StringsRepository repository;

    @Override
    public StringAnalysisResponse analyzeAndStore(StringAnalysisRequest request) {
        if (request.getValue() == null) {
            throw new BadRequestException("Value field is required");
        }

        String value = request.getValue();

        if (repository.findByValue(value).isPresent()) {
            throw new ConflictException("String already exists in the system");
        }

        Strings strings = new Strings();
        strings.setValue(value);

        String hash = StringAnalyzerUtil.calculateSha256Hash(value);
        strings.setId(hash);
        strings.setLength(StringAnalyzerUtil.calculateLength(value));
        strings.setIsPalindrome(StringAnalyzerUtil.isPalindrome(value));
        strings.setUniqueCharacters(StringAnalyzerUtil.countUniqueCharacters(value));
        strings.setWordCount(StringAnalyzerUtil.countWords(value));
        strings.setCharacterFrequencyMap(StringAnalyzerUtil.calculateCharacterFrequency(value));

        Strings saved = repository.save(strings);
        return mapToResponse(saved);
    }

    @Override
    public StringAnalysisResponse getByValue(String value) {
        Strings strings = repository.findByValue(value)
                .orElseThrow(() -> new ResourceNotFoundException("String does not exist in the system"));
        return mapToResponse(strings);
    }

    @Override
    public StringListResponse getAllWithFilters(Boolean isPalindrome, Integer minLength,
                                                Integer maxLength, Integer wordCount,
                                                String containsCharacter) {
        List<Strings> results = repository.findByFilters(isPalindrome, minLength, maxLength, wordCount);

        if (containsCharacter != null && !containsCharacter.isEmpty()) {
            results = results.stream()
                    .filter(strings -> strings.getValue().contains(containsCharacter))
                    .toList();
        }

        List<StringAnalysisResponse> responseList = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        Map<String, Object> filtersApplied = new HashMap<>();
        if (isPalindrome != null) filtersApplied.put("is_palindrome", isPalindrome);
        if (minLength != null) filtersApplied.put("min_length", minLength);
        if (maxLength != null) filtersApplied.put("max_length", maxLength);
        if (wordCount != null) filtersApplied.put("word_count", wordCount);
        if (containsCharacter != null && !containsCharacter.isEmpty()) {
            filtersApplied.put("contains_character", containsCharacter);
        }

        return new StringListResponse(responseList, filtersApplied);
    }

    private StringAnalysisResponse mapToResponse(Strings entity) {
        StringAnalysisResponse response = new StringAnalysisResponse();
        response.setId(entity.getId());
        response.setValue(entity.getValue());
        response.setCreatedAt(entity.getCreatedAt());

        StringAnalysisResponse.StringPropertiesDto properties = new StringAnalysisResponse.StringPropertiesDto();
        properties.setLength(entity.getLength());
        properties.setIsPalindrome(entity.getIsPalindrome());
        properties.setUniqueCharacters(entity.getUniqueCharacters());
        properties.setWordCount(entity.getWordCount());
        properties.setSha256Hash(entity.getId());
        properties.setCharacterFrequencyMap(entity.getCharacterFrequencyMap());

        response.setProperties(properties);
        return response;
    }
}