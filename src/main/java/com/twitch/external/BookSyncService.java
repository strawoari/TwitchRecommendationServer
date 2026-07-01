package com.twitch.external;

import com.twitch.db.BookRepository;
import com.twitch.db.entity.Book;
import com.twitch.db.transformer.BookTransformer;
import com.twitch.model.BookDto;
import com.twitch.model.BookSearchResultDto;
import com.twitch.model.BookTextResponse;
import com.twitch.model.BookWebDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BookSyncService {

    private final BookRepository bookRepository;
    private final BookApiClient bookApiClient;

    public BookSyncService(BookRepository bookRepository, BookApiClient bookApiClient) {
        this.bookRepository = bookRepository;
        this.bookApiClient = bookApiClient;
    }

    /**
     * Gets a BookWebDto by Gutenberg ID, fetching from API if not in database
     */
    @Transactional(readOnly = true)
    public BookWebDto getBookById(Long gutenbergId) {
        if (gutenbergId == null) {
            return null;
        }

        // Try to find in database first
        Optional<Book> bookOpt = bookRepository.findByGutenbergId(gutenbergId);

        if (bookOpt.isPresent()) {
            // Return from database
            return BookTransformer.toBookWebDto(bookOpt.get());
        } else {
            // Fetch from external API
            try {
                BookDto bookDto = bookApiClient.getBookById(gutenbergId);
                if (bookDto != null) {
                    // Fetch sneak peek
                    String sneakPeek = fetchTextSneakPeek(gutenbergId);
                    return BookTransformer.mapToBookWebDto(bookDto, sneakPeek);
                }
            } catch (Exception e) {
                log.error("Failed to fetch book from API for ID {}: {}", gutenbergId, e.getMessage());
            }
        }

        return null;
    }

    @Transactional
    public List<BookWebDto> syncDefaultBooks() {
        log.info("Starting scheduled book synchronization from Gutenberg API");

        List<BookWebDto> lst = new ArrayList<>();
        try {
            int totalPagesToFetch = 5;

            for (int page = 1; page <= totalPagesToFetch; page++) {
                BookSearchResultDto books = bookApiClient.searchBooks(null, page);
                if (books == null || books.results() == null) {
                    log.warn("No results received from Gutenberg API for page {}", page);
                    continue;
                }

                // Process each book to add sneak peek
                for (BookDto bookDto : books.results()) {
                    try {
                        // Fetch text sneak peek for each book
                        String sneakPeek = fetchTextSneakPeek(bookDto.id());
                        BookWebDto bookWebDto = BookTransformer.mapToBookWebDto(bookDto, sneakPeek);
                        lst.add(bookWebDto);

                        // Optionally save to database
                        Book book = BookTransformer.toBookEntity(bookWebDto);
                        bookRepository.save(book);

                        log.info("Synced book: {} (ID: {}) with sneak peek", bookDto.title(), bookDto.id());

                        // Add small delay to avoid rate limiting
                        Thread.sleep(5);

                    } catch (Exception e) {
                        log.error("Failed to process book ID {}: {}", bookDto.id(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during book synchronization", e);
        }

        log.info("Completed synchronization. Total books synced: {}", lst.size());
        return lst;
    }
    /**
     * Fetches the beginning of book text from API and returns it as a sneak peek
     */
    private String fetchTextSneakPeek(Long bookId) {
        try {
            BookTextResponse response = bookApiClient.getBookText(bookId, "simple");
            if (response != null && response.text() != null && !response.text().isBlank()) {
                // Extract first 500 characters or first 3 sentences as sneak peek
                String text = response.text().trim();

                // Option 1: First 500 characters
                if (text.length() > 500) {
                    text = text.substring(0, 500).trim();
                    // Find last sentence ending to avoid cutting mid-sentence
                    int lastPeriod = text.lastIndexOf('.');
                    if (lastPeriod > 400) { // Only if we're past 400 chars
                        text = text.substring(0, lastPeriod + 1);
                    } else {
                        text = text + "...";
                    }
                }
                return text;
            }
        } catch (Exception e) {
            log.warn("Failed to fetch text sneak peek for book ID {}: {}", bookId, e.getMessage());
        }
        return null;
    }
}
