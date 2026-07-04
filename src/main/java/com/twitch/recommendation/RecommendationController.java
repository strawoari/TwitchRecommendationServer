package com.twitch.recommendation;

import com.twitch.model.BookWebDto;
import com.twitch.model.RecommendationResultWebDto;
import com.twitch.security.CustomUserDetails;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feed")
public class RecommendationController {
    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }
    @GetMapping
    public ResponseEntity<RecommendationResultWebDto> getRecommendations(
            Authentication authentication
    ) {
        Long userId = null;

        // Check if user is authenticated (not anonymous)
        if (authentication != null &&
                authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String)) {

            // Extract user ID from CustomUserDetails
            if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                userId = userDetails.getId();
            }
        }

        RecommendationResultWebDto recommendations = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }
}
