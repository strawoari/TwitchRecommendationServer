package com.twitch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookTextResponse(
        @JsonProperty("book_id") Long bookId,
        String title,
        @JsonProperty("alternative_title") String alternativeTitle,
        @JsonProperty("cleaning_mode") String cleaningMode,
        String text,
        Metadata metadata
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(
            @JsonProperty("original_length") Integer originalLength,
            @JsonProperty("cleaned_length") Integer cleanedLength,
            @JsonProperty("source_format") String sourceFormat,
            @JsonProperty("source_url") String sourceUrl
    ) {}
}
