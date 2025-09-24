package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.adapter.in.web.dto.MediaUploadCompleteRequest;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CompleteMediaUploadCommand {

    private final EntityType entityType;
    private final UUID entityId;
    private final List<CompletedUploadInfo> uploads;

    @Getter
    @Builder
    public static class CompletedUploadInfo {
        private final MediaType mediaType;
        private final String publicUrl;
    }

    /**
     * Convert Request DTO to Command DTO
     */
    public static CompleteMediaUploadCommand from(MediaUploadCompleteRequest request) {
        List<CompletedUploadInfo> uploads = request.getUploads().stream()
                .map(upload -> CompletedUploadInfo.builder()
                        .mediaType(upload.getMediaType())
                        .publicUrl(upload.getPublicUrl())
                        .build())
                .toList();

        return CompleteMediaUploadCommand.builder()
                .entityType(request.getEntityType())
                .entityId(request.getEntityId())
                .uploads(uploads)
                .build();
    }
}
