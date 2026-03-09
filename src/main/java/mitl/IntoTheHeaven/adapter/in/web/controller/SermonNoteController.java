package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.sermonnote.CreateSermonNoteRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.sermonnote.SermonNoteResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.sermonnote.UpdateSermonNoteRequest;
import mitl.IntoTheHeaven.application.port.in.command.SermonNoteCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateSermonNoteCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateSermonNoteCommand;
import mitl.IntoTheHeaven.application.port.in.query.SermonNoteQueryUseCase;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.SermonNote;
import mitl.IntoTheHeaven.domain.model.SermonNoteId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Sermon Note", description = "APIs for personal sermon note management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sermon-notes")
public class SermonNoteController {

    private final SermonNoteCommandUseCase sermonNoteCommandUseCase;
    private final SermonNoteQueryUseCase sermonNoteQueryUseCase;

    @Operation(summary = "Get My Sermon Notes", description = "Retrieves all sermon notes belonging to the current user, ordered by sermon date descending.")
    @GetMapping("/me")
    public ResponseEntity<List<SermonNoteResponse>> getMySermonNotes(@AuthenticationPrincipal String memberId) {
        List<SermonNote> sermonNotes = sermonNoteQueryUseCase.getMySermonNotes(
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok(SermonNoteResponse.from(sermonNotes));
    }

    @Operation(summary = "Get Sermon Note Detail", description = "Retrieves a single sermon note by its ID.")
    @GetMapping("/{sermonNoteId}")
    public ResponseEntity<SermonNoteResponse> getSermonNote(@PathVariable UUID sermonNoteId) {
        SermonNote sermonNote = sermonNoteQueryUseCase.getSermonNoteById(
                SermonNoteId.from(sermonNoteId));
        return ResponseEntity.ok(SermonNoteResponse.from(sermonNote));
    }

    @Operation(summary = "Get My Service Types", description = "Retrieves distinct service types the user has previously used, for autocomplete suggestions.")
    @GetMapping("/service-types")
    public ResponseEntity<List<String>> getMyServiceTypes(@AuthenticationPrincipal String memberId) {
        List<String> serviceTypes = sermonNoteQueryUseCase.getMyServiceTypes(
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok(serviceTypes);
    }

    @Operation(summary = "Create Sermon Note", description = "Creates a new personal sermon note.")
    @PostMapping
    public ResponseEntity<SermonNoteResponse> createSermonNote(
            @AuthenticationPrincipal String memberId,
            @RequestBody @Valid CreateSermonNoteRequest request) {
        CreateSermonNoteCommand command = CreateSermonNoteCommand.from(request,
                MemberId.from(UUID.fromString(memberId)));
        SermonNote created = sermonNoteCommandUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(SermonNoteResponse.from(created));
    }

    @Operation(summary = "Update Sermon Note", description = "Updates an existing sermon note.")
    @PutMapping("/{sermonNoteId}")
    public ResponseEntity<SermonNoteResponse> updateSermonNote(
            @PathVariable UUID sermonNoteId,
            @RequestBody @Valid UpdateSermonNoteRequest request) {
        UpdateSermonNoteCommand command = UpdateSermonNoteCommand.from(request,
                SermonNoteId.from(sermonNoteId));
        SermonNote updated = sermonNoteCommandUseCase.update(command);
        return ResponseEntity.ok(SermonNoteResponse.from(updated));
    }

    @Operation(summary = "Delete Sermon Note", description = "Deletes a sermon note by ID (soft delete).")
    @DeleteMapping("/{sermonNoteId}")
    public ResponseEntity<Void> deleteSermonNote(@PathVariable UUID sermonNoteId) {
        sermonNoteCommandUseCase.delete(SermonNoteId.from(sermonNoteId));
        return ResponseEntity.ok().build();
    }
}
