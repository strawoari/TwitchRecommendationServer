package com.twitch.db;

import com.twitch.db.entity.UserBookInteraction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<UserBookInteraction, Long> {
    List<UserBookInteraction> findByUserIdIn(List<Long> userIds);

    List<UserBookInteraction> findByUserId(Long userId);

    List<UserBookInteraction> findByBookIdIn(List<Long> likedBookIds);
}
