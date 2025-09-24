package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.in.command.MediaCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CompleteMediaUploadCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.GeneratePresignedUrlsCommand;
import mitl.IntoTheHeaven.application.port.out.CloudPort;
import mitl.IntoTheHeaven.application.port.out.MediaPort;
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
    private final CloudPort cloudPort;

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
                // Generate unique object key (file path in R2)
                String objectKey = generateObjectKey(mediaType, command);

                // Get max file size for this media type
                Long maxFileSize = getMaxFileSize(mediaType);

                // Generate presigned upload URL
                String uploadUrl = cloudPort.generatePresignedUploadUrl(
                        bucketName, objectKey, command.getContentType(), maxFileSize, expiration);

                // Generate public URL for after upload
                String publicUrl = cloudPort.generatePublicUrl(objectKey);

                // Create upload item
                PresignedUploadInfo.UploadItem uploadItem = PresignedUploadInfo.UploadItem.builder()
                        .mediaType(mediaType)
                        .uploadUrl(uploadUrl)
                        .publicUrl(publicUrl)
                        .build();

                uploads.add(uploadItem);

                log.info("Generated presigned URL for entity {}:{}, mediaType: {}, objectKey: {}",
                        command.getEntityType(), command.getEntityId(), mediaType, objectKey);

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
        
        // Generate fileGroupId for all media in this upload (same original file)
        String fileGroupId = UUID.randomUUID().toString();

        for (CompleteMediaUploadCommand.CompletedUploadInfo upload : command.getUploads()) {
            try {
                // Create Media domain object with provided URL
                Media media = Media.builder()
                        .id(MediaId.from(UUID.randomUUID()))
                        .mediaType(upload.getMediaType())
                        .entityType(command.getEntityType())
                        .entityId(command.getEntityId())
                        .fileGroupId(fileGroupId) // Same fileGroupId for all media in this upload
                        .url(upload.getPublicUrl()) // Use provided URL directly
                        .build();

                // Save to database
                Media savedMedia = mediaPort.save(media);
                savedMedias.add(savedMedia);

                log.info("Successfully completed upload for entity {}:{}, mediaType: {}, url: {}",
                        command.getEntityType(), command.getEntityId(), upload.getMediaType(), upload.getPublicUrl());

            } catch (Exception e) {
                log.error("Failed to complete upload for mediaType: {}, url: {}",
                        upload.getMediaType(), upload.getPublicUrl(), e);
                throw new RuntimeException("Failed to complete upload: " + e.getMessage(), e);
            }
        }

        log.info("Successfully completed {} uploads for entity {}:{}",
                savedMedias.size(), command.getEntityType(), command.getEntityId());

        return savedMedias;
    }

    @Override
    public void deleteById(MediaId mediaId) {
        // 1. Find the media by ID
        Media media = mediaPort.findById(mediaId)
                .orElseThrow(() -> new RuntimeException("Media not found with id: " + mediaId.getValue()));

        // 2. Find all media in the same file group
        List<Media> groupMedias = mediaPort.findByFileGroupId(media.getFileGroupId());

        // 3. Soft delete all media in the group
        groupMedias.forEach(groupMedia -> {
            Media deleted = groupMedia.delete();
            mediaPort.save(deleted);
        });

        log.info("Successfully deleted {} media files in file group: {}", 
                groupMedias.size(), media.getFileGroupId());
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

    /**
     * Generate object key for R2 storage
     */
    private String generateObjectKey(MediaType mediaType, GeneratePresignedUrlsCommand command) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String entityType = command.getEntityType().name();
        String entityId = command.getEntityId().toString();
        String extension = command.getFileName().substring(command.getFileName().lastIndexOf(".") + 1).toLowerCase();
        return String.format("%s_%s_%s_%s.%s",
                entityType,
                entityId,
                mediaType.name().toLowerCase(),
                timestamp,
                extension);
    }
}