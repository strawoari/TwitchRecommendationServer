package com.twitch.db;

import com.twitch.db.entity.User;
import com.twitch.db.entity.UserContact;
import com.twitch.model.ContactStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<UserContact, Long> {

    @EntityGraph(attributePaths = {"requester", "target"})
    List<UserContact> findByRequesterOrTarget(User requester, User target);

    @EntityGraph(attributePaths = {"requester", "target"})
    Optional<UserContact> findByRequesterAndTarget(User requester, User target);

    @EntityGraph(attributePaths = {"requester"})
    List<UserContact> findByTargetAndStatus(User target, ContactStatus status);

    @EntityGraph(attributePaths = {"target"})
    List<UserContact> findByRequesterAndStatus(User requester, ContactStatus status);
}
