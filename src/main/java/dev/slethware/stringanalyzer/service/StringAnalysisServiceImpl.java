package dev.slethware.stringanalyzer.service;

import dev.slethware.stringanalyzer.exception.ConflictException;
import dev.slethware.stringanalyzer.models.dto.NaturalLanguageFilterResponse;
import dev.slethware.stringanalyzer.models.dto.StringAnalysisRequest;
import dev.slethware.stringanalyzer.models.dto.StringAnalysisResponse;
import dev.slethware.stringanalyzer.models.dto.StringListResponse;
import dev.slethware.stringanalyzer.models.entity.Strings;
import dev.slethware.stringanalyzer.exception.BadRequestException;
import dev.slethware.stringanalyzer.exception.ResourceNotFoundException;
import dev.slethware.stringanalyzer.repository.StringsRepository;
import dev.slethware.stringanalyzer.utility.NaturalLanguageQueryParser;
import dev.slethware.stringanalyzer.utility.StringAnalyzerUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StringAnalysisServiceImpl implements StringAnalysisService {

    private final StringsRepository repository;

    @Override
    public StringAnalysisResponse analyzeAndStore(StringAnalysisRequest request) {
        log.info("Starting string analysis for new string submission");

        if (request.getValue() == null) {
            log.error("String analysis failed: Value field is null");
            throw new BadRequestException("Value field is required");
        }

        String value = request.getValue();
        log.debug("Analyzing string with length: {}", value.length());

        if (repository.findByValue(value).isPresent()) {
            log.warn("String analysis failed: Duplicate string detected - '{}'", value);
            throw new ConflictException("String already exists in the system");
        }

        Strings strings = new Strings();
        strings.setValue(value);

        log.debug("Calculating string properties");
        String hash = StringAnalyzerUtil.calculateSha256Hash(value);
        strings.setId(hash);
        strings.setLength(StringAnalyzerUtil.calculateLength(value));
        strings.setIsPalindrome(StringAnalyzerUtil.isPalindrome(value));
        strings.setUniqueCharacters(StringAnalyzerUtil.countUniqueCharacters(value));
        strings.setWordCount(StringAnalyzerUtil.countWords(value));
        strings.setCharacterFrequencyMap(StringAnalyzerUtil.calculateCharacterFrequency(value));

        log.debug("Saving string entity with hash: {}", hash);
        Strings saved = repository.save(strings);
        log.info("Successfully analyzed and stored string with hash: {}, isPalindrome: {}, wordCount: {}",
                hash, saved.getIsPalindrome(), saved.getWordCount());
        return mapToResponse(saved);
    }

    @Override
    public StringAnalysisResponse getByValue(String value) {
        log.info("Retrieving string by value: '{}'", value);
        Strings strings = repository.findByValue(value)
                .orElseThrow(() -> {
                    log.error("String not found: '{}'", value);
                    return new ResourceNotFoundException("String does not exist in the system");
                });
        log.debug("Successfully retrieved string with hash: {}", strings.getId());
        return mapToResponse(strings);
    }

    @Override
    public StringListResponse getAllWithFilters(Boolean isPalindrome, Integer minLength,
                                                Integer maxLength, Integer wordCount,
                                                String containsCharacter) {
        log.info("Fetching strings with filters - isPalindrome: {}, minLength: {}, maxLength: {}, wordCount: {}, containsCharacter: {}",
                isPalindrome, minLength, maxLength, wordCount, containsCharacter);

        List<Strings> results = repository.findByFilters(isPalindrome, minLength, maxLength, wordCount);
        log.debug("Repository query returned {} results", results.size());

        if (containsCharacter != null && !containsCharacter.isEmpty()) {
            int beforeFilter = results.size();
            results = results.stream()
                    .filter(strings -> strings.getValue().contains(containsCharacter))
                    .toList();
            log.debug("After character filter '{}': {} results (filtered out {})",
                    containsCharacter, results.size(), beforeFilter - results.size());
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

        log.info("Successfully retrieved {} strings with applied filters: {}", responseList.size(), filtersApplied);
        return new StringListResponse(responseList, filtersApplied);
    }

    @Override
    public NaturalLanguageFilterResponse filterByNaturalLanguage(String query) {
        log.info("Processing natural language query: '{}'", query);

        Map<String, Object> parsedFilters = NaturalLanguageQueryParser.parseQuery(query);
        log.debug("Parsed filters from natural language query: {}", parsedFilters);

        Boolean isPalindrome = (Boolean) parsedFilters.get("is_palindrome");
        Integer minLength = (Integer) parsedFilters.get("min_length");
        Integer maxLength = (Integer) parsedFilters.get("max_length");
        Integer wordCount = (Integer) parsedFilters.get("word_count");
        String containsCharacter = (String) parsedFilters.get("contains_character");

        List<Strings> results = repository.findByFilters(isPalindrome, minLength, maxLength, wordCount);
        log.debug("Repository query returned {} results", results.size());

        if (containsCharacter != null && !containsCharacter.isEmpty()) {
            int beforeFilter = results.size();
            results = results.stream()
                    .filter(strings -> strings.getValue().contains(containsCharacter))
                    .toList();
            log.debug("After character filter '{}': {} results (filtered out {})",
                    containsCharacter, results.size(), beforeFilter - results.size());
        }

        List<StringAnalysisResponse> responseList = results.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        NaturalLanguageFilterResponse.InterpretedQuery interpretedQuery =
                new NaturalLanguageFilterResponse.InterpretedQuery(query, parsedFilters);

        log.info("Natural language query processed successfully. Found {} results for query: '{}'",
                responseList.size(), query);
        return new NaturalLanguageFilterResponse(responseList, responseList.size(), interpretedQuery);
    }

    @Override
    @Transactional
    public void deleteByValue(String value) {
        log.info("Attempting to delete string by value: '{}'", value);
        Strings strings = repository.findByValue(value)
                .orElseThrow(() -> {
                    log.error("Delete failed: String not found - '{}'", value);
                    return new ResourceNotFoundException("String does not exist in the system");
                });
        log.debug("Deleting string with hash: {}", strings.getId());
        repository.delete(strings);
        log.info("Successfully deleted string with value: '{}' and hash: {}", value, strings.getId());
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