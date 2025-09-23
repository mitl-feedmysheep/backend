package mitl.IntoTheHeaven.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Schema(description = "Request to complete media upload process after files are uploaded to R2")
public class MediaUploadCompleteRequest {

    @NotNull(message = "Entity type is required")
    @Schema(description = "Associated entity type", example = "GROUP")
    private EntityType entityType;

    @NotNull(message = "Entity ID is required")
    @Schema(description = "Associated entity ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID entityId;

    @NotEmpty(message = "Uploads list cannot be empty")
    @Valid
    @Schema(description = "List of completed uploads")
    private List<CompletedUploadItem> uploads;

    @Getter
    @Setter
    @Schema(description = "Individual completed upload item")
    public static class CompletedUploadItem {

        @NotNull(message = "Upload ID is required")
        @Schema(description = "Upload ID from presigned URL response", example = "thumb_20241223_abc123")
        private String uploadId;

        @NotNull(message = "Media type is required")
        @Schema(description = "Type of media", example = "THUMBNAIL")
        private MediaType mediaType;

        @Schema(description = "Final file size uploaded", example = "524288")
        private Long uploadedFileSize;

        @Schema(description = "MD5 hash for integrity check", example = "d41d8cd98f00b204e9800998ecf8427e")
        private String md5Hash;
    }
}
