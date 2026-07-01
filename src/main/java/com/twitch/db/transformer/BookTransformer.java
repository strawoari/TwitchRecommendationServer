package com.twitch.db.transformer;

import com.twitch.db.entity.Book;
import com.twitch.model.AuthorReference;
import com.twitch.model.BookDto;
import com.twitch.model.BookWebDto;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BookTransformer {
    public static BookWebDto toBookWebDto(Book book) {
        if (book == null) {
            return null;
        }

        // Split comma-separated string back into List<String>
        List<String> subjects = null;
        if (book.getSubjectsValue() != null && !book.getSubjectsValue().isBlank()) {
            subjects = Arrays.stream(book.getSubjectsValue().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        // Split comma-separated string into List<AuthorReference>
        List<String> authors = null;
        if (book.getAuthorsValue() != null && !book.getAuthorsValue().isBlank()) {
            authors = Arrays.stream(book.getAuthorsValue().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        return new BookWebDto(
                book.getGutenbergId(),
                book.getTitle(),
                subjects,
                book.getSneakPeek(),
                authors,
                book.getDownloadCount(),
                book.getCoverImage()
        );
    }

    public static BookWebDto mapToBookWebDto(BookDto bookDto, String textPeek) {
        if (bookDto == null) {
            return null;
        }

        List<String> authors = null;
        if (bookDto.authors() != null && !bookDto.authors().isEmpty()) {
            authors = bookDto.authors().stream()
                    .map(AuthorReference::name)
                    .toList();
        }
        return new BookWebDto(
                bookDto.id(),
                bookDto.title(),
                bookDto.subjects(),
                textPeek, // overview - no corresponding field in BookDto
                authors,
                bookDto.downloadCount(),
                bookDto.coverImage()
        );
    }

    public static Book toBookEntity(BookWebDto dto) {
        if (dto == null) {
            return null;
        }

        Book book = new Book();
        book.setGutenbergId(dto.gutenbergId());
        book.setTitle(dto.title());
        book.setSneakPeek(dto.sneakPeek());
        book.setDownloadCount(dto.downloadCount());
        book.setCoverImage(dto.coverImage());

        // Convert List<String> subjects to comma-separated string
        if (dto.subjects() != null && !dto.subjects().isEmpty()) {
            book.setSubjectsValue(String.join(", ", dto.subjects()));
        }

        // Convert List<AuthorReference> to comma-separated string of names
        if (dto.authors() != null && !dto.authors().isEmpty()) {
            String authorsValue = dto.authors().stream()
                    .filter(name -> name != null && !name.isBlank())
                    .collect(Collectors.joining(", "));
            book.setAuthorsValue(authorsValue);
        }

        return book;
    }
}
