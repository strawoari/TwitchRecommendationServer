package com.twitch.user;

import com.twitch.model.FollowRequestDto;
import com.twitch.model.FollowResponseDto;
import com.twitch.model.UserDto;
import com.twitch.model.UserListWebDto;
import com.twitch.security.CustomUserDetails;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping
    public ResponseEntity<FollowResponseDto> followUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody FollowRequestDto request
    ) {
        FollowResponseDto response = followService.followUser(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollowUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long userId
    ) {
        followService.unfollowUser(userDetails.getId(), userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/following")
    public ResponseEntity<UserListWebDto> getFollowing(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(followService.getFollowing(userDetails.getId()));
    }

    @GetMapping("/followers")
    public ResponseEntity<UserListWebDto> getFollowers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(followService.getFollowers(userDetails.getId()));
    }
}
