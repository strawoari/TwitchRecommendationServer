
package org.laioffer.twitch.db.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.laioffer.twitch.external.model.Clip;
import org.laioffer.twitch.external.model.Stream;
import org.laioffer.twitch.external.model.Video;
import org.laioffer.twitch.model.ItemType;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Table("items")
public record ItemEntity(
        @Id Long id,
        @JsonProperty("twitch_id") String twitchId,
        String title,
        String url,
        @JsonProperty("thumbnail_url") String thumbnailUrl,
        @JsonProperty("broadcaster_name") String broadcasterName,
        @JsonProperty("game_id") String gameId,
        @JsonProperty("item_type") ItemType type
) {


    public ItemEntity(String gameId, Video video) {
        this(null, video.id(), video.title(), video.url(), video.thumbnailUrl(), video.userName(), gameId, ItemType.VIDEO);
    }


    public ItemEntity(Clip clip) {
        this(null, clip.id(), clip.title(), clip.url(), clip.thumbnailUrl(), clip.broadcasterName(), clip.gameId(), ItemType.CLIP);
    }


    public ItemEntity(Stream stream) {
        this(null, stream.id(), stream.title(), null, stream.thumbnailUrl(), stream.userName(), stream.gameId(), ItemType.STREAM);
    }
}
