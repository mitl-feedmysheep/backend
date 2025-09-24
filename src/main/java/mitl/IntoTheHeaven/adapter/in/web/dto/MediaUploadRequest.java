package mitl.IntoTheHeaven.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter // Required for @ModelAttribute
@Schema(description = "Media Upload Request DTO")
public class MediaUploadRequest {

    @NotNull(message = "Entity type is required")
    @Schema(description = "Associated entity type (GROUP, GATHERING, MEMBER, CHURCH)", example = "GROUP")
    private EntityType entityType;

    @NotNull(message = "Entity ID is required")
    @Schema(description = "Associated entity ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID entityId;

    @NotNull(message = "File is required")
    @Schema(description = "File to upload")
    private MultipartFile file;

    /**
     * Returns the file extension.
     */
    @Schema(hidden = true) // Hidden from Swagger
    public String getFileExtension() {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    /**
     * Returns the file size in bytes.
     */
    @Schema(hidden = true) // Hidden from Swagger
    public long getFileSize() {
        return file.getSize();
    }

    /**
     * Returns the MIME type of the file.
     */
    @Schema(hidden = true) // Hidden from Swagger
    public String getContentType() {
        return file.getContentType();
    }
}
