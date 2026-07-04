package com.twitch.model;

import java.time.LocalDateTime;

public record ContactResponseDto(
        Long id,
        Long requesterId,
        Long targetId,
        ContactStatus status,
        String targetContactInfo,
        String requesterContactInfo,
        LocalDateTime updatedAt
) {
}
