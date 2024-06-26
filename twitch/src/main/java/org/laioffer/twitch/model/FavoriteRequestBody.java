package org.laioffer.twitch.model;
import org.laioffer.twitch.db.entity.ItemEntity;


public record FavoriteRequestBody(
        ItemEntity favorite
) {}

