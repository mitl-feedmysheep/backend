package mitl.IntoTheHeaven.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.application.dto.PresignedUploadInfo;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "Response containing presigned URLs for image upload")
public class PresignedUrlResponse {

    @Schema(description = "List of presigned upload URLs")
    private final List<PresignedUploadItem> uploads;

    @Schema(description = "Expiration time for all presigned URLs")
    private final LocalDateTime expiresAt;

    @Getter
    @Builder
    @Schema(description = "Individual presigned upload item")
    public static class PresignedUploadItem {

        @Schema(description = "Type of media", example = "THUMBNAIL")
        private final MediaType mediaType;

        @Schema(description = "Unique upload ID for tracking", example = "thumb_20241223_abc123")
        private final String uploadId;

        @Schema(description = "Presigned URL for direct upload to R2", 
                example = "https://my-bucket.r2.dev/uploads/thumb_20241223_abc123.jpg?signature=...")
        private final String uploadUrl;

        @Schema(description = "Final public URL after upload", 
                example = "https://my-bucket.r2.dev/uploads/thumb_20241223_abc123.jpg")
        private final String publicUrl;

        @Schema(description = "Required HTTP method for upload", example = "PUT")
        private final String method;

        @Schema(description = "Maximum file size allowed in bytes", example = "5242880")
        private final Long maxFileSize;
    }

    /**
     * Create response from list of upload items
     */
    public static PresignedUrlResponse of(List<PresignedUploadItem> uploads, LocalDateTime expiresAt) {
        return PresignedUrlResponse.builder()
                .uploads(uploads)
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * Convert domain object to Response DTO
     */
    public static PresignedUrlResponse from(PresignedUploadInfo uploadInfo) {
        List<PresignedUploadItem> uploadItems = uploadInfo.getUploads().stream()
                .map(item -> PresignedUploadItem.builder()
                        .mediaType(item.getMediaType())
                        .uploadId(item.getUploadId())
                        .uploadUrl(item.getUploadUrl())
                        .publicUrl(item.getPublicUrl())
                        .method(item.getMethod())
                        .maxFileSize(item.getMaxFileSize())
                        .build())
                .toList();

        return PresignedUrlResponse.builder()
                .uploads(uploadItems)
                .expiresAt(uploadInfo.getExpiresAt())
                .build();
    }
}
