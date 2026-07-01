package com.twitch.external;

import com.twitch.model.BookDto;
import com.twitch.model.BookSearchResultDto;
import com.twitch.model.BookTextResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gutenberg-api")
public interface BookApiClient {

    /**
     * Handles GET requests to search for books with various optional parameters
     *
     * @param query The search query string
     * @param page_size Number of results per page (optional)
     * @return TmdbSearchResultDto containing the search results
     */
    @GetMapping("/books")
    BookSearchResultDto searchBooks(
            @RequestParam() String query,    // Search query string for finding books
            @RequestParam(required = false) Integer page_size // Number of results per page
    );

    @GetMapping("/books/{id}")
    BookDto getBookById(@PathVariable("id") Long id);

    @GetMapping("/books/{id}/text")
    BookTextResponse getBookText(
            @PathVariable("id") Long id,
            @RequestParam(value = "cleaning_mode", defaultValue = "simple") String cleaningMode
    );
}
