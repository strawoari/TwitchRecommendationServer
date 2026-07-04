package com.twitch.db;

import com.twitch.db.entity.BookRecommendation;
import com.twitch.db.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<BookRecommendation, Long> {

    @EntityGraph(attributePaths = {"sender", "book"})
    List<BookRecommendation> findByReceiver(User receiver);

    @EntityGraph(attributePaths = {"receiver", "book"})
    List<BookRecommendation> findBySender(User sender);
}


