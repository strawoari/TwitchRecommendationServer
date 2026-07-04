package com.twitch.user;

import com.twitch.db.ActivityRepository;
import com.twitch.db.BookRepository;
import com.twitch.db.UserRepository;
import com.twitch.db.entity.Book;
import com.twitch.db.entity.User;
import com.twitch.db.entity.UserBookInteraction;
import com.twitch.model.InteractionRequestDto;
import com.twitch.model.InteractionResponseDto;
import com.twitch.model.InteractionType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InteractionService {

    private final ActivityRepository activityRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public InteractionService(ActivityRepository activityRepository,
                              BookRepository bookRepository,
                              UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public InteractionResponseDto addOrUpdateInteraction(Long userId, InteractionRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (request.type() == InteractionType.RATED && request.rating() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating value is required for RATED type");
        }

        UserBookInteraction interaction = activityRepository
                .findByUserAndBookAndType(user, book, request.type())
                .orElse(null);

        if (interaction == null) {
            interaction = new UserBookInteraction();
            interaction.setUser(user);
            interaction.setBook(book);
            interaction.setType(request.type());
            interaction.setCreatedAt(LocalDateTime.now());
        }

        if (request.type() == InteractionType.RATED) {
            interaction.setRating(request.rating());
        }

        UserBookInteraction saved = activityRepository.save(interaction);
        return toResponseDto(saved);
    }

    @Transactional
    public void removeInteraction(Long userId, Long interactionId) {
        UserBookInteraction interaction = activityRepository.findById(interactionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Interaction not found"));

        if (!interaction.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only remove your own interactions");
        }

        activityRepository.delete(interaction);
    }

    @Transactional(readOnly = true)
    public List<InteractionResponseDto> getMyInteractions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return activityRepository.findByUser(user).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InteractionResponseDto> getMyInteractionsByType(Long userId, InteractionType type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return activityRepository.findByUserAndType(user, type).stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    private InteractionResponseDto toResponseDto(UserBookInteraction interaction) {
        return new InteractionResponseDto(
                interaction.getId(),
                interaction.getUser().getId(),
                interaction.getBook().getId(),
                interaction.getType(),
                interaction.getRating(),
                interaction.getCreatedAt()
        );
    }
}
