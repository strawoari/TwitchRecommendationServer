package com.twitch.external;

import com.twitch.external.model.Clip;
import com.twitch.external.model.Game;
import com.twitch.external.model.Stream;
import com.twitch.external.model.Video;
import com.twitch.model.ChannelInfo;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TwitchService {

    private static final int TWITCH_CHANNEL_BATCH_SIZE = 100; // Twitch API max per request

    private final TwitchApiClient twitchApiClient;

    public TwitchService(TwitchApiClient twitchApiClient) {
        this.twitchApiClient = twitchApiClient;
    }

    @Cacheable("top_games")
    public List<Game> getTopGames() {
        return twitchApiClient.getTopGames().data();
    }

    @Cacheable("games_by_name")
    public List<Game> getGames(String name) {
        return twitchApiClient.getGames(name).data();
    }

    public List<Stream> getStreams(List<String> gameIds, int first) {
        return twitchApiClient.getStreams(gameIds, first).data();
    }

    public List<Video> getVideos(String gameId, int first) {
        return twitchApiClient.getVideos(gameId, first).data();
    }

    public List<Clip> getClips(String gameId, int first) {
        return twitchApiClient.getClips(gameId, first).data();
    }

    public List<String> getTopGameIds() {
        List<String> topGameIds = new ArrayList<>();
        for (Game game : getTopGames()) {
            topGameIds.add(game.id());
        }
        return topGameIds;
    }

    /**
     * Fetches channel information for the given broadcaster IDs.
     * Automatically batches into groups of 100 to respect Twitch API limits.
     */
    public List<ChannelInfo> getChannelInformation(List<String> broadcasterIds) {
        if (broadcasterIds.isEmpty()) {
            return List.of();
        }
        List<ChannelInfo> result = new ArrayList<>();
        for (int i = 0; i < broadcasterIds.size(); i += TWITCH_CHANNEL_BATCH_SIZE) {
            List<String> batch = broadcasterIds.subList(i,
                    Math.min(i + TWITCH_CHANNEL_BATCH_SIZE, broadcasterIds.size()));
            result.addAll(twitchApiClient.getChannelInformation(batch).data());
        }
        return result;
    }
}
