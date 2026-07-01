package com.twitch.external;

import com.twitch.external.model.TmdbMovieDto;
import com.twitch.external.model.TmdbSearchResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gutenberg-api")
public interface BookApiClient {

    @GetMapping("/movie/{movie_id}")
    TmdbMovieDto getMovie(
            @PathVariable("movie_id") long movieId,
            @RequestParam(value = "language", defaultValue = "en-US") String language
    );

/**
 * Handles GET requests to search for movies
 *
 * @param query The search term for movies
 * @param language The language code for results (defaults to "en-US")
 * @param page The page number of results to return (defaults to 1)
 * @return TmdbSearchResultDto containing the search results
 */
    @GetMapping("/search/movie")
    TmdbSearchResultDto searchMovie(
            @RequestParam("query") String query,           // The search query string
            @RequestParam(value = "language", defaultValue = "en-US") String language,  // Language parameter with default value
            @RequestParam(value = "page", defaultValue = "1") int page    // Page number parameter with default value
    );
}
