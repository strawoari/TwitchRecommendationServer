package com.twitch.db;

import com.twitch.db.entity.Movie;
import org.springframework.data.repository.ListCrudRepository;


public interface BookRepository extends ListCrudRepository<Movie, Long> {
}
