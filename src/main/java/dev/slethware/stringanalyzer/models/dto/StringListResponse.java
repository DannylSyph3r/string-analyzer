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
public class StringListResponse {

    private List<StringAnalysisResponse> data;
    private Integer count;

    @JsonProperty("filters_applied")
    private Map<String, Object> filtersApplied;

    public StringListResponse(List<StringAnalysisResponse> data, Map<String, Object> filtersApplied) {
        this.data = data;
        this.count = data.size();
        this.filtersApplied = filtersApplied;
    }
}