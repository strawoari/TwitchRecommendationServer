package com.twitch.db;

import com.twitch.db.entity.User;

import org.springframework.data.repository.ListCrudRepository;


public interface UserRepository extends ListCrudRepository<User, Long> {

}
