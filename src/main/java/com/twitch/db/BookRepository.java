package com.twitch.db;

import com.twitch.db.entity.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findTop100ByOrderByDownloadCountDesc();
    Optional<Book> findByGutenbergId(Long gutenbergId);
    List<Book> findByGutenbergIdIn(List<Long> gutenbergIds);
}
