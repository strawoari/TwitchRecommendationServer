package com.twitch.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InteractionRequestDto(
        @NotNull(message = "Book ID is required")
        Long bookId,

        @NotNull(message = "Interaction type is required")
        InteractionType type,

        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 10, message = "Rating must be at most 10")
        Integer rating
) {
}
