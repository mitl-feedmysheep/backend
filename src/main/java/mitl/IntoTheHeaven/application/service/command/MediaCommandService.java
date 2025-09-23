package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.in.command.MediaCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CompleteMediaUploadCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.GeneratePresignedUrlsCommand;
import mitl.IntoTheHeaven.application.port.out.CloudflareR2Port;
import mitl.IntoTheHeaven.application.port.out.MediaPort;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.MediaId;
import mitl.IntoTheHeaven.application.dto.PresignedUploadInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MediaCommandService implements MediaCommandUseCase {

    private final MediaPort mediaPort;
    private final CloudflareR2Port cloudflareR2Port;

    @Value("${cloudflare.r2.bucket-name}")
    private String bucketName;

    @Value("${cloudflare.r2.presigned-url-expiration-minutes:5}")
    private int presignedUrlExpirationMinutes;

    @Value("${media.max-file-size.thumbnail:2097152}") // 2MB default
    private Long maxThumbnailSize;

    @Value("${media.max-file-size.medium:5242880}") // 5MB default
    private Long maxMediumSize;

    @Override
    public PresignedUploadInfo generatePresignedUrls(GeneratePresignedUrlsCommand command) {
        List<PresignedUploadInfo.UploadItem> uploads = new ArrayList<>();
        Duration expiration = Duration.ofMinutes(presignedUrlExpirationMinutes);

        // Auto-generate THUMBNAIL and MEDIUM for each request
        MediaType[] mediaTypes = { MediaType.THUMBNAIL, MediaType.MEDIUM };

        for (MediaType mediaType : mediaTypes) {
            try {
                // Generate unique upload ID
                String uploadId = cloudflareR2Port.generateUploadId(mediaType);

                // Generate object key (file path in R2)
                String objectKey = cloudflareR2Port.generateObjectKey(uploadId, mediaType, command.getFileName());

                // Get max file size for this media type
                Long maxFileSize = getMaxFileSize(mediaType);

                // Generate presigned upload URL
                String uploadUrl = cloudflareR2Port.generatePresignedUploadUrl(
                        bucketName, objectKey, command.getContentType(), maxFileSize, expiration);

                // Generate public URL for after upload
                String publicUrl = cloudflareR2Port.generatePublicUrl(bucketName, objectKey);

                // Create upload item
                PresignedUploadInfo.UploadItem uploadItem = PresignedUploadInfo.UploadItem.builder()
                        .mediaType(mediaType)
                        .uploadId(uploadId)
                        .uploadUrl(uploadUrl)
                        .publicUrl(publicUrl)
                        .method("PUT")
                        .maxFileSize(maxFileSize)
                        .build();

                uploads.add(uploadItem);

                log.info("Generated presigned URL for entity {}:{}, mediaType: {}, uploadId: {}",
                        command.getEntityType(), command.getEntityId(), mediaType, uploadId);

            } catch (Exception e) {
                log.error("Failed to generate presigned URL for mediaType: {}", mediaType, e);
                throw new RuntimeException("Failed to generate presigned URL: " + e.getMessage(), e);
            }
        }

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(presignedUrlExpirationMinutes);
        return PresignedUploadInfo.of(uploads, expiresAt);
    }

    /**
     * Complete upload process after files are uploaded to R2 via presigned URLs
     */
    @Override
    public List<Media> completeUpload(CompleteMediaUploadCommand command) {
        List<Media> savedMedias = new ArrayList<>();

        for (CompleteMediaUploadCommand.CompletedUploadInfo upload : command.getUploads()) {
            try {
                // Generate object key from upload ID
                String objectKey = cloudflareR2Port.generateObjectKey(upload.getUploadId(), upload.getMediaType(), "");

                // Generate public URL
                String publicUrl = cloudflareR2Port.generatePublicUrl(bucketName, objectKey);

                // Verify file exists (optional, but good for reliability)
                if (!cloudflareR2Port.fileExists(bucketName, objectKey)) {
                    log.warn("File not found in R2 for uploadId: {}, objectKey: {}", upload.getUploadId(), objectKey);
                    // Continue anyway, might be eventual consistency
                }

                // Create Media domain object
                Media media = Media.builder()
                        .id(MediaId.from(UUID.randomUUID()))
                        .mediaType(upload.getMediaType())
                        .entityType(command.getEntityType())
                        .entityId(command.getEntityId())
                        .storagePath(null) // Not needed for R2
                        .url(publicUrl) // R2 public URL
                        .build();

                // Save to database
                Media savedMedia = mediaPort.save(media);
                savedMedias.add(savedMedia);

                log.info("Successfully completed upload for entity {}:{}, mediaType: {}, uploadId: {}",
                        command.getEntityType(), command.getEntityId(), upload.getMediaType(), upload.getUploadId());

            } catch (Exception e) {
                log.error("Failed to complete upload for uploadId: {}, mediaType: {}",
                        upload.getUploadId(), upload.getMediaType(), e);
                throw new RuntimeException("Failed to complete upload: " + e.getMessage(), e);
            }
        }

        log.info("Successfully completed {} uploads for entity {}:{}",
                savedMedias.size(), command.getEntityType(), command.getEntityId());

        return savedMedias;
    }

    @Override
    public void deleteMediaByEntity(EntityType entityType, UUID entityId) {
        // 1. Retrieve all media for the given entity
        List<Media> medias = mediaPort.findByEntity(entityType, entityId);

        // 2. Soft delete each media (R2 files will be cleaned up separately)
        medias.forEach(media -> {
            Media deleted = media.delete();
            mediaPort.save(deleted);
        });

        log.info("Successfully deleted {} media files for entity {}:{}",
                medias.size(), entityType, entityId);
    }

    /**
     * Get maximum file size for media type
     */
    private Long getMaxFileSize(MediaType mediaType) {
        return switch (mediaType) {
            case THUMBNAIL -> maxThumbnailSize;
            case MEDIUM -> maxMediumSize;
        };
    }
}