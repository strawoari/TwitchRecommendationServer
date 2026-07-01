package com.twitch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RecommendationResultWebDto (
        @JsonProperty("friend_approved") List<BookWebDto> fromFriends,
        @JsonProperty("for_you") List<BookWebDto> forYou
){
}
