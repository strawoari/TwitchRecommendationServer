package com.twitch.model;
import com.twitch.db.entity.ItemEntity;


public record FavoriteRequestBody(
        ItemEntity favorite
) {}