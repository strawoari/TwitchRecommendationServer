package com.twitch.user;

import com.twitch.model.InteractionRequestDto;
import com.twitch.model.InteractionResponseDto;
import com.twitch.model.InteractionType;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping
    public ResponseEntity<InteractionResponseDto> addOrUpdateInteraction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody InteractionRequestDto request
    ) {
        InteractionResponseDto response = interactionService.addOrUpdateInteraction(userDetails.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{interactionId}")
    public ResponseEntity<Void> removeInteraction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long interactionId
    ) {
        interactionService.removeInteraction(userDetails.getId(), interactionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<InteractionResponseDto>> getMyInteractions(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) InteractionType type
    ) {
        List<InteractionResponseDto> interactions;
        if (type != null) {
            interactions = interactionService.getMyInteractionsByType(userDetails.getId(), type);
        } else {
            interactions = interactionService.getMyInteractions(userDetails.getId());
        }
        return ResponseEntity.ok(interactions);
    }
}
