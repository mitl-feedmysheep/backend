package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.adapter.in.web.dto.MediaUploadRequest;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Builder
public class MediaUploadCommand {

    private final EntityType entityType;
    private final UUID entityId;
    private final MultipartFile file;
    private final String fileExtension;
    private final long fileSize;
    private final String contentType;

    /**
     * Request DTO → Command DTO
     */
    public static MediaUploadCommand from(MediaUploadRequest request) {
        return MediaUploadCommand.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .file(request.getFile())
                .fileExtension(request.getFileExtension())
                .fileSize(request.getFileSize())
                .contentType(request.getContentType())
                .build();
    }
}
