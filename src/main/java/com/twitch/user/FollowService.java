package com.twitch.user;

import com.twitch.db.FollowRepository;
import com.twitch.db.UserRepository;
import com.twitch.db.entity.Follow;
import com.twitch.db.entity.User;
import com.twitch.model.FollowRequestDto;
import com.twitch.model.FollowResponseDto;
import com.twitch.model.UserDto;
import com.twitch.model.UserListWebDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public FollowResponseDto followUser(Long followerId, FollowRequestDto request) {
        Long followingId = request.userId();

        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot follow yourself");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to follow not found"));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You are already following this user");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setCreatedAt(LocalDateTime.now());

        Follow saved = followRepository.save(follow);
        return toResponseDto(saved);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot unfollow yourself");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User to unfollow not found"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Follow relationship not found"));

        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public UserListWebDto getFollowing(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<UserDto> users = followRepository.findByFollower(currentUser).stream()
                .map(follow -> {
                    User followedUser = follow.getFollowing();
                    boolean followsBack = followRepository.existsByFollowerAndFollowing(followedUser, currentUser);
                    return toUserDto(followedUser, follow.getCreatedAt(), followsBack);
                })
                .collect(Collectors.toList());
        return new UserListWebDto(users);
    }

    @Transactional(readOnly = true)
    public UserListWebDto getFollowers(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<UserDto> users = followRepository.findByFollowing(currentUser).stream()
                .map(follow -> {
                    User follower = follow.getFollower();
                    boolean followsBack = followRepository.existsByFollowerAndFollowing(currentUser, follower);
                    return toUserDto(follower, follow.getCreatedAt(), followsBack);
                })
                .collect(Collectors.toList());
        return new UserListWebDto(users);
    }

    private FollowResponseDto toResponseDto(Follow follow) {
        return new FollowResponseDto(
                follow.getId(),
                follow.getFollower().getId(),
                follow.getFollowing().getId(),
                follow.getCreatedAt()
        );
    }

    private UserDto toUserDto(User user, LocalDateTime followedAt, boolean followsBack) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getAvatarUrl(),
                followedAt,
                followsBack
        );
    }
}
