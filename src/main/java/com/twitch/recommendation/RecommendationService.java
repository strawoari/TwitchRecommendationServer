package com.twitch.recommendation;
import com.twitch.db.entity.ItemEntity;
import com.twitch.db.entity.UserEntity;
import com.twitch.external.TwitchService;
import com.twitch.external.model.Clip;
import com.twitch.external.model.Stream;
import com.twitch.external.model.Video;
import com.twitch.favorite.FavoriteService;
import com.twitch.model.TypeGroupedItemList;
import com.twitch.model.ChannelInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;


@Service
public class RecommendationService {


    private static final int MAX_GAME_SEED = 3;
    private static final int PER_PAGE_ITEM_SIZE = 20;

    private static final Set<String> GUEST_CCL_EXCLUSIONS = Set.of(
            "SexualThemes", "DrugsIntoxication", "Gambling", "MatureGame"
    );
    private final TwitchService twitchService;
    private final FavoriteService favoriteService;


    public RecommendationService(TwitchService twitchService, FavoriteService favoriteService) {
        this.twitchService = twitchService;
        this.favoriteService = favoriteService;
    }

    public TypeGroupedItemList recommendItems(UserEntity userEntity, Set<String> cclExclusions) {
        List<String> gameIds;
        Set<String> favoriteExclusions = new HashSet<>();
        Set<String> effectiveCclExclusions;

        if (userEntity == null) {
            gameIds = twitchService.getTopGameIds();
            effectiveCclExclusions = GUEST_CCL_EXCLUSIONS;         // always enforce for guests
        } else {
            List<ItemEntity> items = favoriteService.getFavoriteItems(userEntity);
            if (items.isEmpty()) {
                gameIds = twitchService.getTopGameIds();
            } else {
                Set<String> uniqueGameIds = new HashSet<>();
                for (ItemEntity item : items) {
                    uniqueGameIds.add(item.gameId());
                    favoriteExclusions.add(item.twitchId());
                }
                gameIds = new ArrayList<>(uniqueGameIds);
            }
            // logged-in users supply their own list; empty set = no CCL filtering
            effectiveCclExclusions = (cclExclusions != null) ? cclExclusions : Set.of();
        }

        int gameSize = Math.min(gameIds.size(), MAX_GAME_SEED);
        int perGameListSize = PER_PAGE_ITEM_SIZE / gameSize;

        List<ItemEntity> streams = recommendStreams(gameIds, favoriteExclusions, effectiveCclExclusions);
        List<ItemEntity> clips   = recommendClips(gameIds.subList(0, gameSize), perGameListSize, favoriteExclusions, effectiveCclExclusions);
        List<ItemEntity> videos  = recommendVideos(gameIds.subList(0, gameSize), perGameListSize, favoriteExclusions, effectiveCclExclusions);

        return new TypeGroupedItemList(streams, videos, clips);
    }

// ── CCL helper ────────────────────────────────────────────────────────────────

    /**
     * Returns the set of broadcaster IDs whose channels carry at least one
     * of the excluded CCL labels. Batches the lookup so it is a single API call.
     */
    private Set<String> cclFilteredBroadcasters(List<String> broadcasterIds, Set<String> cclExclusions) {
        if (cclExclusions.isEmpty() || broadcasterIds.isEmpty()) {
            return Set.of();
        }
        return twitchService.getChannelInformation(broadcasterIds).stream()
                .filter(ch -> ch.contentClassificationLabels()
                        .stream()
                        .anyMatch(cclExclusions::contains))
                .map(ChannelInfo::broadcasterId)
                .collect(Collectors.toSet());
    }

// ── recommend helpers ─────────────────────────────────────────────────────────

    private List<ItemEntity> recommendStreams(
            List<String> gameIds, Set<String> exclusions, Set<String> cclExclusions) {

        List<Stream> streams = twitchService.getStreams(gameIds, PER_PAGE_ITEM_SIZE);

        // batch: one channel-info call for all broadcaster IDs
        Set<String> blockedBroadcasters = cclFilteredBroadcasters(
                streams.stream().map(Stream::userId).collect(Collectors.toList()),
                cclExclusions);

        List<ItemEntity> result = new ArrayList<>();
        for (Stream stream : streams) {
            if (!exclusions.contains(stream.id()) && !blockedBroadcasters.contains(stream.userId())) {
                result.add(new ItemEntity(stream));
            }
        }
        return result;
    }

    private List<ItemEntity> recommendVideos(
            List<String> gameIds, int perGameListSize,
            Set<String> exclusions, Set<String> cclExclusions) {

        List<ItemEntity> result = new ArrayList<>();
        for (String gameId : gameIds) {
            List<Video> listPerGame = twitchService.getVideos(gameId, perGameListSize);

            Set<String> blockedBroadcasters = cclFilteredBroadcasters(
                    listPerGame.stream().map(Video::userId).collect(Collectors.toList()),
                    cclExclusions);

            for (Video video : listPerGame) {
                if (!exclusions.contains(video.id()) && !blockedBroadcasters.contains(video.userId())) {
                    result.add(new ItemEntity(gameId, video));
                }
            }
        }
        return result;
    }

    private List<ItemEntity> recommendClips(
            List<String> gameIds, int perGameListSize,
            Set<String> exclusions, Set<String> cclExclusions) {

        List<ItemEntity> result = new ArrayList<>();
        for (String gameId : gameIds) {
            List<Clip> listPerGame = twitchService.getClips(gameId, perGameListSize);

            Set<String> blockedBroadcasters = cclFilteredBroadcasters(
                    listPerGame.stream().map(Clip::broadcasterId).collect(Collectors.toList()),
                    cclExclusions);

            for (Clip clip : listPerGame) {
                if (!exclusions.contains(clip.id()) && !blockedBroadcasters.contains(clip.broadcasterId())) {
                    result.add(new ItemEntity(clip));
                }
            }
        }
        return result;
    }
}
