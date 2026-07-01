

package com.twitch.recommendation;

import com.twitch.db.ActivityRepository;
import com.twitch.db.BookRepository;
import com.twitch.db.FollowRepository;
import com.twitch.db.entity.Book;
import com.twitch.db.entity.Follow;
import com.twitch.db.entity.UserBookInteraction;
import com.twitch.db.transformer.BookTransformer;
import com.twitch.external.BookApiClient;
import com.twitch.external.BookSyncService;
import com.twitch.model.BookDto;
import com.twitch.model.BookWebDto;
import com.twitch.model.InteractionType;
import com.twitch.model.RecommendationResultWebDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecommendationService {

    private final BookSyncService bookSyncService;
    private final BookRepository bookRepository;
    private final ActivityRepository activityRepository;
    private final FollowRepository followRepository;
    private final int recommendationLimit = 100;

    public RecommendationService(
            BookSyncService bookSyncService,
            BookRepository bookRepository,
            ActivityRepository activityRepository,
            FollowRepository followRepository
    ) {
        this.bookSyncService = bookSyncService;
        this.bookRepository = bookRepository;
        this.activityRepository = activityRepository;
        this.followRepository = followRepository;
    }

    @Transactional(readOnly = true)
    public ResponseEntity<RecommendationResultWebDto> getRecommendations(Long userId) {
        if (userId == null) {
            // Personalized recommendations for logged-in user
            return new ResponseEntity<>(
                    new RecommendationResultWebDto(
                            new ArrayList<>(),
                            bookSyncService.syncDefaultBooks()
                    ),
                    HttpStatus.OK);
        } else {
            // Generic/trending recommendations for logged in users
            List<BookWebDto> fromFriends = getRecommendationsFromFriends(userId);

            // 2. Get personalized recommendations (Collaborative Filtering)
            List<BookWebDto> forYou = getPersonalizedRecommendations(userId);

            return new ResponseEntity<>(
                    new RecommendationResultWebDto(fromFriends, forYou),
                    HttpStatus.OK
            );
        }
    }

    /**
     * Fetches books that the user's friends have liked or rated highly.
     */
    private List<BookWebDto> getRecommendationsFromFriends(Long userId) {
        // 1. Find IDs of users that the current user follows
        List<Long> friendIds = followRepository.findByFollowerId(userId)
                .stream()
                .map(Follow::getFollowingId)
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. Get interactions from these friends
        List<UserBookInteraction> friendInteractions = activityRepository.findByUserIdIn(friendIds);

        // 3. Filter for positive interactions (LIKED or RATED >= 4) and extract book IDs
        Set<Long> friendBookIds = friendInteractions.stream()
                .filter(i -> i.getType() == InteractionType.LIKED ||
                        (i.getType() == InteractionType.RATED && i.getRating() != null && i.getRating() >= 4))
                .map(UserBookInteraction::getBookId) // Note: Assuming 'movieId' is a typo for 'bookId'
                .collect(Collectors.toSet());

        // 4. Fetch books and map to DTOs
        return bookRepository.findAllById(friendBookIds)
                .stream()
                .limit(recommendationLimit)
                .map(BookTransformer::toBookWebDto)
                .collect(Collectors.toList());
    }

    /**
     * Simple User-Based Collaborative Filtering:
     * Finds users who liked the same books as the current user,
     * and recommends other books those similar users liked.
     */
    private List<BookWebDto> getPersonalizedRecommendations(Long userId) {
        // 1. Get the current user's interactions
        List<UserBookInteraction> userInteractions = activityRepository.findByUserId(userId);

        // Books the user has already interacted with (to exclude them from recommendations)
        Set<Long> userBookIds = userInteractions.stream()
                .map(UserBookInteraction::getBookId)
                .collect(Collectors.toSet());

        // Books the user explicitly liked or rated highly
        List<Long> likedBookIds = userInteractions.stream()
                .filter(i -> i.getType() == InteractionType.LIKED ||
                        (i.getType() == InteractionType.RATED && i.getRating() != null && i.getRating() >= 4))
                .map(UserBookInteraction::getBookId)
                .collect(Collectors.toList());

        Set<Long> recommendedBookIds = new HashSet<>();

        if (!likedBookIds.isEmpty()) {
            // 2. Find interactions on the books the user liked
            List<UserBookInteraction> similarInteractions = activityRepository.findByBookIdIn(likedBookIds);

            // 3. Extract IDs of OTHER users who liked the same books
            List<Long> similarUserIds = similarInteractions.stream()
                    .map(UserBookInteraction::getUserId)
                    .filter(id -> !id.equals(userId)) // Exclude the current user
                    .distinct()
                    .collect(Collectors.toList());

            if (!similarUserIds.isEmpty()) {
                // 4. Find books liked by these similar users
                List<UserBookInteraction> similarUserInteractions = activityRepository.findByUserIdIn(similarUserIds);

                recommendedBookIds = similarUserInteractions.stream()
                        .filter(i -> i.getType() == InteractionType.LIKED ||
                                (i.getType() == InteractionType.RATED && i.getRating() != null && i.getRating() >= 4))
                        .map(UserBookInteraction::getBookId)
                        .filter(id -> !userBookIds.contains(id)) // Exclude books the user already knows about
                        .collect(Collectors.toSet());
            }
        }

        // 5. Fetch recommended books, or fallback to popular books if no collaborative matches found
        List<Book> books;
        if (!recommendedBookIds.isEmpty()) {
            books = bookRepository.findAllById(recommendedBookIds);
        } else {
            // Fallback: Recommend most downloaded books if the user has no interactions or no similar users
            books = bookRepository.findTop100OrderByDownloadCountDesc();
        }

        return books.stream()
                .limit(recommendationLimit)
                .map(BookTransformer::toBookWebDto)
                .collect(Collectors.toList());
    }
}
