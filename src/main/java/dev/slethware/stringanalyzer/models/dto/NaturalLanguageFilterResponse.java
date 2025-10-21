package dev.slethware.stringanalyzer.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class NaturalLanguageFilterResponse {

    private List<StringAnalysisResponse> data;
    private Integer count;

    @JsonProperty("interpreted_query")
    private InterpretedQuery interpretedQuery;



    @Getter
    @Setter
    @AllArgsConstructor
    public static class InterpretedQuery {
        private String original;

        @JsonProperty("parsed_filters")
        private Map<String, Object> parsedFilters;
    }
}