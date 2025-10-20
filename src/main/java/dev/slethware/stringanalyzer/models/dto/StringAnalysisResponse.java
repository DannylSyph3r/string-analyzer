package dev.slethware.stringanalyzer.models .dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class StringAnalysisResponse {

    private String id;
    private String value;
    private StringPropertiesDto properties;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    public static class StringPropertiesDto {

        private Integer length;

        @JsonProperty("is_palindrome")
        private Boolean isPalindrome;

        @JsonProperty("unique_characters")
        private Integer uniqueCharacters;

        @JsonProperty("word_count")
        private Integer wordCount;

        @JsonProperty("sha256_hash")
        private String sha256Hash;

        @JsonProperty("character_frequency_map")
        private Map<String, Integer> characterFrequencyMap;
    }
}