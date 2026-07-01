package com.twitch.external.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BookSearchResultDto(
        @JsonProperty("page") Integer page,
        @JsonProperty("results") List<BookDto> results,
        @JsonProperty("total_pages") Integer totalPages,
        @JsonProperty("total_results") Integer totalResults
) {
}
