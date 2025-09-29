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
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.MediaId;
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

    @Operation(summary = "Delete Media by ID", description = "Deletes a specific media file (includes all sizes from same original file)")
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Void> deleteMediaById(
            @Parameter(description = "Media ID") @PathVariable UUID mediaId) {
        mediaCommandUseCase.deleteById(MediaId.from(mediaId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete All Media by Entity ID", description = "Deletes all media files associated with a specific entity ID")
    @DeleteMapping("/entity/{entityId}")
    public ResponseEntity<Void> deleteMediaByEntityId(
            @Parameter(description = "Entity ID to delete all associated media") @PathVariable UUID entityId) {
        mediaCommandUseCase.deleteByEntity(entityId);
        return ResponseEntity.noContent().build();
    }
}
