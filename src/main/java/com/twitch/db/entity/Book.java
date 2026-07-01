package com.twitch.db.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;


@Entity
@Table(name = "books")
public class Book {
    @Id @GeneratedValue
    private Long id;
    private Long tmdbId;
    private String title;
    private String posterUrl;
    private String backdropUrl;
    private LocalDateTime releaseDate;
    private String genreValue;
    private Boolean adult;
    private Integer runtime;
    private String languageValue;
    private String overview;
    private String tagline;
    private String homepageUrl;
}
