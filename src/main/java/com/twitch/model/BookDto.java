package com.twitch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookDto(
        Long id,
        String title,
        @JsonProperty("alternative_title") String alternativeTitle,
        List<AuthorReference> authors,
        List<String> subjects,
        @JsonProperty("bookshelves") List<String> bookshelves,
        @JsonProperty("media_type") String mediaType,
        @JsonProperty("download_count") Integer downloadCount,
        String issued,
        @JsonProperty("reading_ease_score") String readingEaseScore,
        @JsonProperty("cover_image") String coverImage
) {
}
