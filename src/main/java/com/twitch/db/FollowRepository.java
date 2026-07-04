package com.twitch.db;

import com.twitch.db.entity.Follow;
import com.twitch.db.entity.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    @EntityGraph(attributePaths = {"following"})
    List<Follow> findByFollower(User follower);

    @EntityGraph(attributePaths = {"follower", "following"})
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    boolean existsByFollowerAndFollowing(User follower, User following);

    @EntityGraph(attributePaths = {"follower"})
    List<Follow> findByFollowing(User following);

}
