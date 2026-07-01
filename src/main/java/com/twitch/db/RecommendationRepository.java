package com.twitch.db;


import com.twitch.db.entity.BookRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RecommendationRepository extends JpaRepository<BookRecommendation, Long> {
}


