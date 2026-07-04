package com.twitch.external;

import com.twitch.db.BookRepository;
import com.twitch.db.entity.Book;
import com.twitch.db.transformer.BookTransformer;
import com.twitch.model.BookDto;
import com.twitch.model.BookSearchResultDto;
import com.twitch.model.BookTextResponse;
import com.twitch.model.BookWebDto;
import com.twitch.model.SearchResultWebDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookSyncService {

    private final BookRepository bookRepository;
    private final BookApiClient bookApiClient;

    public BookSyncService(BookRepository bookRepository, BookApiClient bookApiClient) {
        this.bookRepository = bookRepository;
        this.bookApiClient = bookApiClient;
    }

    @Transactional(readOnly = true)
    public BookWebDto getBookById(Long gutenbergId) {
        if (gutenbergId == null) return null;

        Optional<Book> bookOpt = bookRepository.findByGutenbergId(gutenbergId);
        if (bookOpt.isPresent()) {
            return BookTransformer.toBookWebDto(bookOpt.get());
        } else {
            try {
                BookDto bookDto = bookApiClient.getBookById(gutenbergId);
                if (bookDto != null) {
                    return BookTransformer.mapToBookWebDto(bookDto, bookDto.summary());
                }
            } catch (Exception e) {
                log.error("Failed to fetch book from API for ID {}: {}", gutenbergId, e.getMessage());
            }
        }
        return null;
    }

    @Transactional
    public SearchResultWebDto searchBooks(String query, int page) {
        BookSearchResultDto results = bookApiClient.searchBooks(query.trim(), page);

        // Null safety for new API response structure
        if (results == null || results.results() == null) {
            return new SearchResultWebDto(Collections.emptyList());
        }

        List<BookWebDto> books = results.results().stream()
                .map(book -> BookTransformer.mapToBookWebDto(book, book.summary()))
                .collect(Collectors.toList());

        return new SearchResultWebDto(books);
    }

    @Transactional
    public List<BookWebDto> getDefaultBooks() {
        List<BookWebDto> lst = new ArrayList<>();
        int oneTimeLimit = 5;

        try {
            int totalPagesToFetch = 1;
            for (int page = 1; page <= totalPagesToFetch; page++) {
                if (oneTimeLimit <= 0) break;
                BookSearchResultDto books = bookApiClient.searchBooks(null, page);

                if (books == null || books.results() == null) continue;

                for (BookDto bookDto : books.results()) {
                    if (oneTimeLimit <= 0) break;
                    try {
                        // USE THE SUMMARY INSTEAD OF FETCHING TEXT!
                        BookWebDto bookWebDto = BookTransformer.mapToBookWebDto(bookDto, bookDto.summary());
                        lst.add(bookWebDto);
                        oneTimeLimit--;
                    } catch (Exception e) {
                        log.error("Failed to process book ID {}: {}", bookDto.id(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error during book synchronization", e);
        }

        return lst;
    }

}