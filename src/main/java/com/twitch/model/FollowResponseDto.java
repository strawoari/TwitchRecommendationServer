package com.twitch.model;

import java.time.LocalDateTime;

public record FollowResponseDto(
        Long id,
        Long followerId,
        Long followingId,
        LocalDateTime createdAt
) {
}
