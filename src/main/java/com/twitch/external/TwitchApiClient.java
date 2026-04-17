package com.twitch.external;

import com.twitch.external.model.ChannelInfoResponse;
import com.twitch.external.model.ClipResponse;
import com.twitch.external.model.GameResponse;
import com.twitch.external.model.StreamResponse;
import com.twitch.external.model.VideoResponse;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "twitch-api")
public interface TwitchApiClient {

    @GetMapping("/games")
    GameResponse getGames(@RequestParam("name") String name);

    @GetMapping("/games/top")
    GameResponse getTopGames();

    @GetMapping("/videos/")
    VideoResponse getVideos(@RequestParam("game_id") String gameId, @RequestParam("first") int first);

    @GetMapping("/clips/")
    ClipResponse getClips(@RequestParam("game_id") String gameId, @RequestParam("first") int first);

    @GetMapping("/streams/")
    StreamResponse getStreams(@RequestParam("game_id") List<String> gameIds, @RequestParam("first") int first);

    @GetMapping("/channels")
    ChannelInfoResponse getChannelInformation(@RequestParam("broadcaster_id") List<String> broadcasterIds);
}
