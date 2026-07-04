package com.twitch.external;

import com.twitch.db.transformer.BookTransformer;
import com.twitch.model.BookSearchResultDto;
import com.twitch.model.BookWebDto;
import com.twitch.model.SearchResultWebDto;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookSyncService bookSyncService;

    // Removed unused BookApiClient dependency
    public BookController(BookSyncService bookSyncService) {
        this.bookSyncService = bookSyncService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookWebDto> getBook(@PathVariable Long gutenbergId) {
        BookWebDto book = bookSyncService.getBookById(gutenbergId);
        if (book == null) {
            return ResponseEntity.notFound().build(); // Better practice than returning 200 OK with null
        }
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public ResponseEntity<SearchResultWebDto> searchBooks(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "1") Integer page) {

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        SearchResultWebDto result = bookSyncService.searchBooks(query.trim(), page);
        return ResponseEntity.ok(result);
    }
}
