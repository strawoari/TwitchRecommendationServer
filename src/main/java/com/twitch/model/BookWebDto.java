package com.twitch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BookWebDto(
        Long gutenbergId,
        String title,
        List<String> subjects,
        @JsonProperty("sneak_peek") String sneakPeek,
        List<String> authors,
        @JsonProperty("download_count") Integer downloadCount,
        @JsonProperty("cover_image") String coverImage
) {
}
