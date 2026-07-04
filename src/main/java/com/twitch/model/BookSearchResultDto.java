package com.twitch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookSearchResultDto(
        String next,
        String previous,
        @JsonProperty("results") List<BookDto> results
) {
}
