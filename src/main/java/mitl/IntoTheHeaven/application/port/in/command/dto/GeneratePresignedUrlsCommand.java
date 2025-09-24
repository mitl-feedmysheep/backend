package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.adapter.in.web.dto.PresignedUrlRequest;
import mitl.IntoTheHeaven.domain.enums.EntityType;

import java.util.UUID;

@Getter
@Builder
public class GeneratePresignedUrlsCommand {

    private final EntityType entityType;
    private final UUID entityId;
    private final String fileName;
    private final String contentType;
    private final Long fileSize;

    /**
     * Convert Request DTO to Command DTO
     */
    public static GeneratePresignedUrlsCommand from(PresignedUrlRequest request) {
        return GeneratePresignedUrlsCommand.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .fileName(request.getFileName())
                .contentType(request.getContentType())
                .fileSize(request.getFileSize())
                .build();
    }
}
