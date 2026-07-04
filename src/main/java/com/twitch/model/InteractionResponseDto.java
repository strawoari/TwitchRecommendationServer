package com.twitch.model;

import java.time.LocalDateTime;

public record InteractionResponseDto(
        Long id,
        Long userId,
        Long bookId,
        InteractionType type,
        Integer rating,
        LocalDateTime createdAt
) {
}
