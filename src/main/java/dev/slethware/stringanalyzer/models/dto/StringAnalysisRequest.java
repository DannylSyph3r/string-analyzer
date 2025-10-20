package dev.slethware.stringanalyzer.models.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StringAnalysisRequest {

    @NotNull(message = "Value Field is Required")
    private String value;
}
