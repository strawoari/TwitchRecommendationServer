package com.twitch.db;

import com.twitch.db.entity.Book;
import com.twitch.db.entity.User;
import com.twitch.db.entity.UserBookInteraction;
import com.twitch.model.InteractionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<UserBookInteraction, Long> {

    @EntityGraph(attributePaths = {"book"})
    List<UserBookInteraction> findByUserIn(List<User> users);

    @EntityGraph(attributePaths = {"book"})
    List<UserBookInteraction> findByUser(User user);

    @EntityGraph(attributePaths = {"user"})
    List<UserBookInteraction> findByBookIn(List<Book> books);

    @EntityGraph(attributePaths = {"user", "book"})
    Optional<UserBookInteraction> findByUserAndBookAndType(User user, Book book, InteractionType type);

    boolean existsByUserAndBookAndType(User user, Book book, InteractionType type);

    @EntityGraph(attributePaths = {"book"})
    List<UserBookInteraction> findByUserAndType(User user, InteractionType type);
}
