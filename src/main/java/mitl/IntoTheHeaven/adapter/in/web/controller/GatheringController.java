package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.CreateGatheringRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.CreateGatheringResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.GatheringDetailResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.UpdateGatheringMemberRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.UpdateGatheringMemberResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.UpdateGatheringRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.UpdateGatheringResponse;
import mitl.IntoTheHeaven.application.port.in.command.GatheringCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateGatheringCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringMemberCommand;
import mitl.IntoTheHeaven.application.port.in.query.GatheringQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.command.UpdateGatheringUseCase;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GatheringMember;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Gathering", description = "APIs for Gathering Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/gatherings")
public class GatheringController {

    private final GatheringQueryUseCase gatheringQueryUseCase;
    private final GatheringCommandUseCase gatheringCommandUseCase;
    private final UpdateGatheringUseCase updateGatheringUseCase;

    @Operation(summary = "Create New Gathering", description = "Creates a new gathering with the specified details including date, time, place, and description.")
    @PostMapping
    public ResponseEntity<CreateGatheringResponse> createGathering(
            @Valid @RequestBody CreateGatheringRequest request
    ) {
        CreateGatheringCommand command = CreateGatheringCommand.from(request);
        Gathering gathering = gatheringCommandUseCase.createGathering(command);
        CreateGatheringResponse response = CreateGatheringResponse.from(gathering);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get Gathering Details", description = "Retrieves detailed information of a specific gathering, including its members and prayers.")
    @GetMapping("/{gatheringId}")
    public ResponseEntity<GatheringDetailResponse> getGatheringDetail(
            @PathVariable UUID gatheringId
    ) {
        Gathering gathering = gatheringQueryUseCase.getGatheringDetail(GatheringId.from(gatheringId));
        GatheringDetailResponse response = GatheringDetailResponse.from(gathering);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Gathering Member and create prayer requests", description = "Updates attendance, story sharing, and prayer requests for a specific gathering member.")
    @PatchMapping("/{gatheringId}/groupMember/{groupMemberId}")
    public ResponseEntity<UpdateGatheringMemberResponse> updateGatheringMember(
            @PathVariable UUID gatheringId,
            @PathVariable UUID groupMemberId,
            @Valid @RequestBody UpdateGatheringMemberRequest request
    ) {
        UpdateGatheringMemberCommand command = UpdateGatheringMemberCommand.from(
            GatheringId.from(gatheringId), 
            GroupMemberId.from(groupMemberId), 
            request
        );
        GatheringMember gatheringMember = gatheringCommandUseCase.updateGatheringMember(command);
        UpdateGatheringMemberResponse response = UpdateGatheringMemberResponse.from(gatheringMember);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Gathering", description = "Partially updates gathering fields: name, description, date, startedAt, endedAt, place.")
    @PatchMapping("/{gatheringId}")
    public ResponseEntity<UpdateGatheringResponse> updateGathering(
            @PathVariable UUID gatheringId,
            @Valid @RequestBody UpdateGatheringRequest request
    ) {
        UpdateGatheringCommand command = UpdateGatheringCommand.from(GatheringId.from(gatheringId), request);
        Gathering updated = updateGatheringUseCase.updateGathering(command);
        UpdateGatheringResponse response = UpdateGatheringResponse.from(updated);
        return ResponseEntity.ok(response);
    }
} 