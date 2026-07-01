package com.twitch.external;

import com.twitch.external.model.BookSearchResultDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies")
public class BookController {

    private final BookApiClient bookApiClient;

    public BookController(BookApiClient bookApiClient) {
        this.bookApiClient = bookApiClient;
    }

    @GetMapping("/search")
    public ResponseEntity<BookSearchResultDto> searchMovies(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "1") Integer page) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        BookSearchResultDto results = bookApiClient.searchBooks(query.trim(), page);
        return ResponseEntity.ok(results);
    }
}
