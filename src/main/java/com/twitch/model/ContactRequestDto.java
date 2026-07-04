package com.twitch.model;

import jakarta.validation.constraints.NotNull;

public record ContactRequestDto(
        @NotNull(message = "Target user ID is required")
        Long targetUserId,

        String contactInfo
) {
}
