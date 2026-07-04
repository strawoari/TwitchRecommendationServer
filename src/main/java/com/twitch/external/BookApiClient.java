package com.twitch.external;

import com.twitch.AppConfig;
import com.twitch.model.BookDto;
import com.twitch.model.BookSearchResultDto;
import com.twitch.model.BookTextResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "gutenberg-api",
        url = "https://project-gutenberg-free-books-api1.p.rapidapi.com",
        configuration = FeignConfig.class
)
public interface BookApiClient {

    @GetMapping("/books")
    BookSearchResultDto searchBooks(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", required = false) Integer page
    );

    @GetMapping("/books/{id}")
    BookDto getBookById(@PathVariable("id") Long id);
}
