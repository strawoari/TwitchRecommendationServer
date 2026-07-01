package com.twitch.db.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a user entity in the database.
 * This record is annotated to be mapped to the "users" table in the database.
 *
 *
 * @param id The unique identifier of the user
 *           username The username of the user
 *           email The email address of the user
 *           passwordHash The hashed password of the user
 *           bio The bio of the user
 *           avatarUrl The URL of the user's avatar image
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity                              // Marks this class as a JPA entity, mapped to a database table
@Table(name = "users")              // Specifies the table name in the database to which this entity will be mapped
public class User {                  // Defines the User class which represents the user entity
    @Id @GeneratedValue
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String bio;
    private String avatarUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "privacy_setting_id", referencedColumnName = "id")
    private PrivacySetting privacySetting;
}