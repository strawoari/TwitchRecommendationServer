package com.twitch.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue
    private Long id;

    private Long gutenbergId;
    private String title;
    private String subjectsValue;
    private String sneakPeek;
    private String authorsValue;
    private Integer downloadCount;
    private String coverImage;
}
