package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CompleteMediaUploadCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.GeneratePresignedUrlsCommand;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.application.dto.PresignedUploadInfo;

import java.util.List;
import java.util.UUID;

public interface MediaCommandUseCase {

    /**
     * Generate presigned URLs for direct upload to R2
     * 
     * @param command Command containing entity info and file details
     * @return Domain object with presigned upload information
     */
    PresignedUploadInfo generatePresignedUrls(GeneratePresignedUrlsCommand command);

    /**
     * Complete upload after files are uploaded to R2 via presigned URLs
     * 
     * @param command Upload completion command
     * @return Created media entities (THUMBNAIL, MEDIUM)
     */
    List<Media> completeUpload(CompleteMediaUploadCommand command);

    /**
     * Delete all media associated with a specific entity (soft delete)
     */
    void deleteMediaByEntity(EntityType entityType, UUID entityId);
}
