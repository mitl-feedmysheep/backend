package mitl.IntoTheHeaven.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.domain.model.Media;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Schema(description = "Response after successfully completing media upload")
public class MediaUploadCompleteResponse {

    @Schema(description = "List of created media entities")
    private final List<CreatedMediaItem> medias;

    @Schema(description = "Total number of media files created")
    private final int totalCount;

    @Schema(description = "Upload completion timestamp")
    private final LocalDateTime completedAt;

    @Getter
    @Builder
    @Schema(description = "Individual created media item")
    public static class CreatedMediaItem {

        @Schema(description = "Media ID", example = "123e4567-e89b-12d3-a456-426614174000")
        private final UUID mediaId;

        @Schema(description = "Type of media", example = "THUMBNAIL")
        private final MediaType mediaType;

        @Schema(description = "Public URL for accessing the media", 
                example = "https://my-bucket.r2.dev/uploads/thumb_20241223_abc123.jpg")
        private final String publicUrl;

        @Schema(description = "Creation timestamp")
        private final LocalDateTime createdAt;
    }

    /**
     * Create response from list of Media domain objects
     */
    public static MediaUploadCompleteResponse from(List<Media> medias) {
        List<CreatedMediaItem> mediaItems = medias.stream()
                .map(media -> CreatedMediaItem.builder()
                        .mediaId(media.getId().getValue())
                        .mediaType(media.getMediaType())
                        .publicUrl(media.getUrl())
                        .createdAt(media.getCreatedAt())
                        .build())
                .toList();

        return MediaUploadCompleteResponse.builder()
                .medias(mediaItems)
                .totalCount(mediaItems.size())
                .completedAt(LocalDateTime.now())
                .build();
    }
}
