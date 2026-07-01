package com.twitch.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "book_recommendations")
public class BookRecommendation {
    @Id
    @GeneratedValue
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Long bookId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
