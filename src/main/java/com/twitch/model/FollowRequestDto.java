package com.twitch.model;

import jakarta.validation.constraints.NotNull;

public record FollowRequestDto(
        @NotNull(message = "User ID to follow is required")
        Long userId
) {
}
