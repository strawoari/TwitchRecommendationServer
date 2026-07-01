package com.twitch.db;

import com.twitch.db.entity.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<UserContact, Long> {

}
