package com.twitch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ChannelInfo(
        @JsonProperty("broadcaster_id") String broadcasterId,
        @JsonProperty("broadcaster_login") String broadcasterLogin,     //
        @JsonProperty("broadcaster_name") String broadcasterName,      // broadcaster_name
        @JsonProperty("game_id") String gameId,               // game_id
        @JsonProperty("game_name") String gameName,             // game_name
        @JsonProperty("title") String title,
        @JsonProperty("content_classification_labels") List<String> contentClassificationLabels
) {
}
