package com.twitch.db.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Represents a user entity in the database.
 * This record is annotated to be mapped to the "users" table in the database.
 *
 * @param id The unique identifier of the user
 * @param username The username of the user
 * @param firstName The first name of the user
 * @param lastName The last name of the user
 * @param password The password of the user (should be stored securely)
 */

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
}