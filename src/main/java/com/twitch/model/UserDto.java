package com.twitch.model;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        String email,
        String bio,
        String avatarUrl,
        LocalDateTime followedAt,
        boolean followsBack
) {}
