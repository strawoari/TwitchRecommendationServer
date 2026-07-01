package com.twitch.model;

import java.util.List;

public record SearchResultWebDto (
        List<BookWebDto> books
){
}
