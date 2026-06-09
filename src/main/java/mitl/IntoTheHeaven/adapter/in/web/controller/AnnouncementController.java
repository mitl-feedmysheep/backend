package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.announcement.AnnouncementResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.announcement.CreateAnnouncementRequest;
import mitl.IntoTheHeaven.application.port.in.command.AnnouncementCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.AnnouncementQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Announcement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Announcement", description = "APIs for Announcement Management")
@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementQueryUseCase announcementQueryUseCase;
    private final AnnouncementCommandUseCase announcementCommandUseCase;

    @Operation(summary = "Get Recent 2 Announcements", description = "Returns the 2 most recent announcements for the given entity. Used on the home screen.")
    @GetMapping("/recent")
    public ResponseEntity<List<AnnouncementResponse>> getRecent2(
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") String entityId) {
        List<Announcement> announcements = announcementQueryUseCase.getRecent2(entityType, entityId);
        return ResponseEntity.ok(AnnouncementResponse.from(announcements));
    }

    @Operation(summary = "Get Announcement List", description = "Returns all announcements for the given entity, sorted by latest first.")
    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> getList(
            @RequestParam("entityType") String entityType,
            @RequestParam("entityId") String entityId) {
        List<Announcement> announcements = announcementQueryUseCase.getList(entityType, entityId);
        return ResponseEntity.ok(AnnouncementResponse.from(announcements));
    }

    @Operation(summary = "Get Announcement Detail")
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> getById(@PathVariable("id") UUID id) {
        Announcement announcement = announcementQueryUseCase.getById(id);
        return ResponseEntity.ok(AnnouncementResponse.from(announcement));
    }

    @Operation(summary = "Create Announcement", description = "Admin only. Creates a new scheduled announcement.")
    @PostMapping
    public ResponseEntity<AnnouncementResponse> create(@Valid @RequestBody CreateAnnouncementRequest request) {
        Announcement announcement = announcementCommandUseCase.create(
                request.getEntityType(),
                request.getEntityId(),
                request.getTitle(),
                request.getBody(),
                request.getSendAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(AnnouncementResponse.from(announcement));
    }

    @Operation(summary = "Delete Announcement", description = "Admin only. Soft deletes an announcement.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        announcementCommandUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}
