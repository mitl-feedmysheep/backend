package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.education.CreateEducationProgramRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.education.EducationProgramResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.education.EducationProgressResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.education.GraduateMemberRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.education.RecordEducationProgressRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.education.UpdateEducationProgramRequest;
import mitl.IntoTheHeaven.application.port.in.command.EducationCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateEducationProgramCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.GraduateMemberCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.RecordEducationProgressCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateEducationProgramCommand;
import mitl.IntoTheHeaven.application.port.in.query.EducationQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.dto.EducationProgramWithProgress;
import mitl.IntoTheHeaven.domain.model.EducationProgress;
import mitl.IntoTheHeaven.domain.model.EducationProgressId;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Education", description = "APIs for Education Program and Progress Management")
@RestController
@RequiredArgsConstructor
public class EducationController {

    private final EducationCommandUseCase educationCommandUseCase;
    private final EducationQueryUseCase educationQueryUseCase;

    @Operation(summary = "Create Education Program", description = "Creates an education program for a group.")
    @PostMapping("/groups/{groupId}/education-program")
    public ResponseEntity<EducationProgramResponse> createProgram(
            @PathVariable UUID groupId,
            @RequestBody @Valid CreateEducationProgramRequest request) {
        CreateEducationProgramCommand command = new CreateEducationProgramCommand(
                GroupId.from(groupId),
                request.getName(),
                request.getDescription(),
                request.getTotalWeeks());
        educationCommandUseCase.createProgram(command);

        EducationProgramWithProgress data = educationQueryUseCase.getProgramWithProgress(GroupId.from(groupId));
        return ResponseEntity.status(HttpStatus.CREATED).body(EducationProgramResponse.from(data));
    }

    @Operation(summary = "Get Education Program", description = "Retrieves the education program with all members' progress for a group.")
    @GetMapping("/groups/{groupId}/education-program")
    public ResponseEntity<EducationProgramResponse> getProgram(@PathVariable UUID groupId) {
        EducationProgramWithProgress data = educationQueryUseCase.getProgramWithProgress(GroupId.from(groupId));
        return ResponseEntity.ok(EducationProgramResponse.from(data));
    }

    @Operation(summary = "Update Education Program", description = "Updates the education program configuration.")
    @PutMapping("/groups/{groupId}/education-program")
    public ResponseEntity<EducationProgramResponse> updateProgram(
            @PathVariable UUID groupId,
            @RequestBody @Valid UpdateEducationProgramRequest request) {
        EducationProgramWithProgress existing = educationQueryUseCase.getProgramWithProgress(GroupId.from(groupId));
        UpdateEducationProgramCommand command = new UpdateEducationProgramCommand(
                existing.getProgram().getId(),
                request.getName(),
                request.getDescription(),
                request.getTotalWeeks());
        educationCommandUseCase.updateProgram(command);

        EducationProgramWithProgress updated = educationQueryUseCase.getProgramWithProgress(GroupId.from(groupId));
        return ResponseEntity.ok(EducationProgramResponse.from(updated));
    }

    @Operation(summary = "Record Education Progress", description = "Records a completed education week for a member in a gathering.")
    @PostMapping("/gatherings/{gatheringId}/education-progress")
    public ResponseEntity<EducationProgressResponse> recordProgress(
            @PathVariable UUID gatheringId,
            @RequestBody @Valid RecordEducationProgressRequest request) {
        RecordEducationProgressCommand command = new RecordEducationProgressCommand(
                GatheringId.from(gatheringId),
                GroupMemberId.from(request.getGroupMemberId()),
                request.getWeekNumber());
        EducationProgress progress = educationCommandUseCase.recordProgress(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(EducationProgressResponse.from(progress));
    }

    @Operation(summary = "Remove Education Progress", description = "Removes a completed education week record (hard delete).")
    @DeleteMapping("/gatherings/{gatheringId}/education-progress/{progressId}")
    public ResponseEntity<Void> removeProgress(
            @PathVariable UUID gatheringId,
            @PathVariable UUID progressId) {
        educationCommandUseCase.removeProgress(EducationProgressId.from(progressId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get Education Progress by Gathering", description = "Retrieves all education progress records for a specific gathering.")
    @GetMapping("/gatherings/{gatheringId}/education-progress")
    public ResponseEntity<List<EducationProgressResponse>> getProgressByGathering(
            @PathVariable UUID gatheringId) {
        List<EducationProgress> progressList = educationQueryUseCase.getProgressByGathering(
                GatheringId.from(gatheringId));
        return ResponseEntity.ok(EducationProgressResponse.from(progressList));
    }

    @Operation(summary = "Graduate Member", description = "Graduates a member from the newcomer group to a target group. Increments graduated count and transfers the member.")
    @PostMapping("/groups/{groupId}/graduate")
    public ResponseEntity<Void> graduateMember(
            @PathVariable UUID groupId,
            @RequestBody @Valid GraduateMemberRequest request) {
        GraduateMemberCommand command = new GraduateMemberCommand(
                GroupId.from(groupId),
                GroupMemberId.from(request.getGroupMemberId()),
                GroupId.from(request.getTargetGroupId()));
        educationCommandUseCase.graduateMember(command);
        return ResponseEntity.ok().build();
    }
}
