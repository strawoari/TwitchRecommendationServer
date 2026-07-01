package com.twitch.db.entity;


import com.twitch.model.InteractionType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import jakarta.persistence.Id;


import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_book_interactions")
public class UserBookInteraction {
    @Id @GeneratedValue
    private Long id;
    private Long userId;
    private Long movieId;

    @Enumerated(EnumType.STRING)
    private InteractionType type; // LIKED, WATCHLIST, RATED

    private Integer rating; // 1-10
    private LocalDateTime createdAt;
}
