package com.twitch.recommendation;

import com.twitch.db.entity.UserEntity;
import com.twitch.model.TypeGroupedItemList;
import com.twitch.user.UserService;
import java.util.Set;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationController {


    private final RecommendationService recommendationService;
    private final UserService userService;


    public RecommendationController(
            RecommendationService recommendationService,
            UserService userService
    ) {
        this.recommendationService = recommendationService;
        this.userService = userService;
    }

    @GetMapping("/recommendation")
    public TypeGroupedItemList getRecommendation(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Set<String> cclExclusions) {

        UserEntity userEntity = null;
        if (user != null) {
            userEntity = userService.findByUsername(user.getUsername());
        }
        return recommendationService.recommendItems(userEntity, cclExclusions);
    }
}