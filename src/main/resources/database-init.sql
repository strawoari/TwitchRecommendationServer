USE twitch;

DROP TABLE IF EXISTS user_movie_interactions;
DROP TABLE IF EXISTS movie_recommendations;
DROP TABLE IF EXISTS user_contacts;
DROP TABLE IF EXISTS follows;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS privacy_settings;
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255),
    password_hash VARCHAR(255),
    bio TEXT,
    avatar_url VARCHAR(500),
    privacy_setting_id BIGINT
);

CREATE TABLE privacy_settings
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    allow_contact_requests TINYINT NOT NULL DEFAULT 1,
    show_watch_history TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE follows
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL,
    following_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movies
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tmdb_id BIGINT,
    title VARCHAR(500),
    poster_url VARCHAR(500),
    backdrop_url VARCHAR(500),
    release_date DATETIME,
    genre_value VARCHAR(255),
    adult TINYINT,
    runtime INTEGER,
    language_value VARCHAR(50),
    overview TEXT,
    tagline VARCHAR(500),
    homepage_url VARCHAR(500)
);

CREATE TABLE movie_recommendations
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    message TEXT,
    is_read TINYINT NOT NULL DEFAULT 0
);

CREATE TABLE user_contacts
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    requester_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    target_contact_info VARCHAR(255),
    requester_contact_info VARCHAR(255)
);

CREATE TABLE user_movie_interactions
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    rating INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
