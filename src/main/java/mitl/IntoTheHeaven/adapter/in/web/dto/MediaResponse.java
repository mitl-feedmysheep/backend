package mitl.IntoTheHeaven.adapter.in.web.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.domain.model.Media;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MediaResponse {

    private final UUID id;
    private final MediaType mediaType;
    private final EntityType entityType;
    private final UUID entityId;
    private final String url;
    private final LocalDateTime createdAt;

    /**
     * Domain Model → Response DTO
     */
    public static MediaResponse from(Media media) {
        return MediaResponse.builder()
                .id(media.getId().getValue())
                .mediaType(media.getMediaType())
                .entityType(media.getEntityType())
                .entityId(media.getEntityId())
                .url(media.getUrl())
                .createdAt(media.getCreatedAt())
                .build();
    }

    /**
     * Domain Model List → Response DTO List
     */
    public static List<MediaResponse> from(List<Media> medias) {
        return medias.stream()
                .map(MediaResponse::from)
                .toList();
    }
}
