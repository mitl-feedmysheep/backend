package mitl.IntoTheHeaven.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import mitl.IntoTheHeaven.domain.enums.EntityType;

import java.util.UUID;

@Getter
@Setter
@Schema(description = "Request for generating presigned URLs for image upload (auto-generates THUMBNAIL + MEDIUM)")
public class PresignedUrlRequest {

    @NotNull(message = "Entity type is required")
    @Schema(description = "Associated entity type", example = "GROUP")
    private EntityType entityType;

    @NotNull(message = "Entity ID is required")
    @Schema(description = "Associated entity ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID entityId;

    @NotNull(message = "Original file name is required")
    @Schema(description = "Original file name (will generate thumb/medium versions)", example = "my-vacation-photo.jpg")
    private String fileName;

    @NotNull(message = "Content type is required")
    @Schema(description = "Content type", example = "image/jpeg", defaultValue = "image/jpeg")
    private String contentType;

    @NotNull(message = "Expected file size is required")
    @Schema(description = "Expected file size in bytes", example = "2048576")
    private Long fileSize;
}
