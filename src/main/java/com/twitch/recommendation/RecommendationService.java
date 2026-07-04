package com.twitch.recommendation;

import com.twitch.db.ActivityRepository;
import com.twitch.db.BookRepository;
import com.twitch.db.FollowRepository;
import com.twitch.db.UserRepository;
import com.twitch.db.entity.Book;
import com.twitch.db.entity.Follow;
import com.twitch.db.entity.User;
import com.twitch.db.entity.UserBookInteraction;
import com.twitch.db.transformer.BookTransformer;
import com.twitch.external.BookSyncService;
import com.twitch.model.InteractionType;
import com.twitch.model.RecommendationResultWebDto;
import com.twitch.model.BookWebDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecommendationService {

    private final BookSyncService bookSyncService;
    private final BookRepository bookRepository;
    private final ActivityRepository activityRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final int recommendationLimit = 100;

    public RecommendationService(
            BookSyncService bookSyncService,
            BookRepository bookRepository,
            ActivityRepository activityRepository,
            FollowRepository followRepository,
            UserRepository userRepository
    ) {
        this.bookSyncService = bookSyncService;
        this.bookRepository = bookRepository;
        this.activityRepository = activityRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public RecommendationResultWebDto getRecommendations(Long userId) {
        if (userId == null) {
            return new RecommendationResultWebDto(
                    new ArrayList<>(),
                    bookSyncService.getDefaultBooks()
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 1. Get recommendations from friends
        List<BookWebDto> fromFriends = getRecommendationsFromFriends(user);

        // 2. Get personalized recommendations (Collaborative Filtering)
        List<BookWebDto> forYou = getPersonalizedRecommendations(user);

        return new RecommendationResultWebDto(fromFriends, forYou);
    }

    /**
     * Fetches books that the user's friends have liked or rated highly.
     */
    private List<BookWebDto> getRecommendationsFromFriends(User user) {
        // 1. Find users that the current user follows
        List<User> friends = followRepository.findByFollower(user)
                .stream()
                .map(Follow::getFollowing)
                .collect(Collectors.toList());

        if (friends.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Get interactions from these friends
        List<UserBookInteraction> friendInteractions = activityRepository.findByUserIn(friends);

        // 3. Filter for positive interactions (LIKED or RATED >= 4) and extract books
        Set<Book> friendBooks = friendInteractions.stream()
                .filter(i -> i.getType() == InteractionType.LIKED ||
                        (i.getType() == InteractionType.RATED && i.getRating() != null && i.getRating() >= 4))
                .map(UserBookInteraction::getBook)
                .collect(Collectors.toSet());

        // 4. Map to DTOs
        return friendBooks.stream()
                .limit(recommendationLimit)
                .map(BookTransformer::toBookWebDto)
                .collect(Collectors.toList());
    }

    /**
     * Simple User-Based Collaborative Filtering:
     * Finds users who liked the same books as the current user,
     * and recommends other books those similar users liked.
     */
    private List<BookWebDto> getPersonalizedRecommendations(User user) {
        // 1. Get the current user's interactions
        List<UserBookInteraction> userInteractions = activityRepository.findByUser(user);

        // Books the user has already interacted with (to exclude them from recommendations)
        Set<Book> userBooks = userInteractions.stream()
                .map(UserBookInteraction::getBook)
                .collect(Collectors.toSet());

        // Books the user explicitly liked or rated highly
        List<Book> likedBooks = userInteractions.stream()
                .filter(i -> i.getType() == InteractionType.LIKED ||
                        (i.getType() == InteractionType.RATED && i.getRating() != null && i.getRating() >= 4))
                .map(UserBookInteraction::getBook)
                .collect(Collectors.toList());

        Set<Book> recommendedBooks = new HashSet<>();

        if (!likedBooks.isEmpty()) {
            // 2. Find interactions on the books the user liked
            List<UserBookInteraction> similarInteractions = activityRepository.findByBookIn(likedBooks);

            // 3. Extract OTHER users who liked the same books
            List<User> similarUsers = similarInteractions.stream()
                    .map(UserBookInteraction::getUser)
                    .filter(u -> !u.getId().equals(user.getId())) // Exclude the current user
                    .distinct()
                    .collect(Collectors.toList());

            if (!similarUsers.isEmpty()) {
                // 4. Find books liked by these similar users
                List<UserBookInteraction> similarUserInteractions = activityRepository.findByUserIn(similarUsers);

                recommendedBooks = similarUserInteractions.stream()
                        .filter(i -> i.getType() == InteractionType.LIKED ||
                                (i.getType() == InteractionType.RATED && i.getRating() != null && i.getRating() >= 4))
                        .map(UserBookInteraction::getBook)
                        .filter(b -> !userBooks.contains(b)) // Exclude books the user already knows about
                        .collect(Collectors.toSet());
            }
        }

        // 5. Fetch recommended books, or fallback to popular books if no collaborative matches found
        List<Book> books;
        if (!recommendedBooks.isEmpty()) {
            books = new ArrayList<>(recommendedBooks);
        } else {
            // Fallback: Recommend most downloaded books if the user has no interactions or no similar users
            books = bookRepository.findTop100ByOrderByDownloadCountDesc();
        }

        return books.stream()
                .limit(recommendationLimit)
                .map(BookTransformer::toBookWebDto)
                .collect(Collectors.toList());
    }
}
