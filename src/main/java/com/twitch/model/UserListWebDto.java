package com.twitch.model;

import java.util.List;

public record UserListWebDto(
        List<UserDto> users
) {}