package com.twitch.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book_recommendations")
public class BookRecommendation {
    @Id
    @GeneratedValue
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long movieId;
    private String message;
    private boolean isRead;
}
