package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.PresignedUrlRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.PresignedUrlResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.MediaUploadCompleteRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.MediaUploadCompleteResponse;
import mitl.IntoTheHeaven.application.port.in.command.MediaCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.GeneratePresignedUrlsCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.CompleteMediaUploadCommand;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.application.dto.PresignedUploadInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Media", description = "APIs for Media Management (Presigned URLs, Upload & Delete)")
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaCommandUseCase mediaCommandUseCase;

    @Operation(summary = "Generate Presigned URLs", description = "Generates presigned URLs for direct upload to Cloudflare R2 (typically for THUMBNAIL + MEDIUM)")
    @PostMapping("/presigned-urls")
    public ResponseEntity<PresignedUrlResponse> generatePresignedUrls(
            @Parameter(description = "Presigned URL request") @Valid @RequestBody PresignedUrlRequest request) {
        GeneratePresignedUrlsCommand command = GeneratePresignedUrlsCommand.from(request);
        PresignedUploadInfo uploadInfo = mediaCommandUseCase.generatePresignedUrls(command);
        PresignedUrlResponse response = PresignedUrlResponse.from(uploadInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Complete Media Upload", description = "Completes the media upload process after files are uploaded to R2 via presigned URLs")
    @PostMapping("/complete")
    public ResponseEntity<MediaUploadCompleteResponse> completeUpload(
            @Parameter(description = "Upload completion request") @Valid @RequestBody MediaUploadCompleteRequest request) {
        CompleteMediaUploadCommand command = CompleteMediaUploadCommand.from(request);
        List<Media> medias = mediaCommandUseCase.completeUpload(command);
        return ResponseEntity.ok(MediaUploadCompleteResponse.from(medias));
    }

    @Operation(summary = "Delete All Media by Entity", description = "Deletes all media associated with a specific entity")
    @DeleteMapping
    public ResponseEntity<Void> deleteMediaByEntity(
            @Parameter(description = "Entity type (GROUP, GATHERING, MEMBER, CHURCH)") @RequestParam EntityType entityType,
            @Parameter(description = "Entity ID") @RequestParam UUID entityId) {
        mediaCommandUseCase.deleteMediaByEntity(entityType, entityId);
        return ResponseEntity.noContent().build();
    }
}
