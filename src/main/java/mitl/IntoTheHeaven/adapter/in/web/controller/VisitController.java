package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminAddVisitMembersRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminCreateVisitRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminUpdateVisitRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminVisitListResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminVisitResponse;
import mitl.IntoTheHeaven.application.port.in.command.VisitCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.AddVisitMembersCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.query.VisitQueryUseCase;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import mitl.IntoTheHeaven.domain.model.VisitMemberId;
import mitl.IntoTheHeaven.global.aop.RequireChurchRole;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Visit", description = "APIs for Visit Management (Admin Only)")
@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitQueryUseCase visitQueryUseCase;
    private final VisitCommandUseCase visitCommandUseCase;

    /* ADMIN */
    @Operation(summary = "Create Visit", description = "ADMIN - Create a new visit record")
    @PostMapping("/admin")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<AdminVisitResponse> createVisit(
            @Valid @RequestBody AdminCreateVisitRequest request,
            @AuthenticationPrincipal String memberId,
            @CurrentSecurityContext(expression = "authentication.churchId") String churchId) {
        CreateVisitCommand command = AdminCreateVisitRequest.toCommand(request,
                ChurchId.from(UUID.fromString(churchId)),
                MemberId.from(UUID.fromString(memberId)));
        Visit visit = visitCommandUseCase.createVisit(command);
        AdminVisitResponse response = AdminVisitResponse.from(visit);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* ADMIN */
    @Operation(summary = "Get All My Visits", description = "ADMIN - Get all visits for current church and member (ordered by date desc)")
    @GetMapping("/admin")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<List<AdminVisitListResponse>> getAllVisits(
            @AuthenticationPrincipal String memberId,
            @CurrentSecurityContext(expression = "authentication.churchId") String churchId) {
        List<Visit> visits = visitQueryUseCase.getAllVisits(ChurchId.from(UUID.fromString(churchId)),
                MemberId.from(UUID.fromString(memberId)));
        List<AdminVisitListResponse> response = AdminVisitListResponse.from(visits);
        return ResponseEntity.ok(response);
    }

    /* ADMIN */
    @Operation(summary = "Get Visit Detail", description = "ADMIN - Get visit details by ID including members and prayers")
    @GetMapping("/admin/{visitId}")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<AdminVisitResponse> getVisitDetail(
            @PathVariable UUID visitId) {
        Visit visit = visitQueryUseCase.getVisitById(VisitId.from(visitId));
        AdminVisitResponse response = AdminVisitResponse.from(visit);
        return ResponseEntity.ok(response);
    }

    /* ADMIN */
    @Operation(summary = "Update Visit", description = "ADMIN - Update visit information including members and prayers")
    @PutMapping("/admin/{visitId}")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<AdminVisitResponse> updateVisit(
            @PathVariable UUID visitId,
            @Valid @RequestBody AdminUpdateVisitRequest request) {
        UpdateVisitCommand command = AdminUpdateVisitRequest.toCommand(request);
        Visit visit = visitCommandUseCase.updateVisit(VisitId.from(visitId), command);
        AdminVisitResponse response = AdminVisitResponse.from(visit);
        return ResponseEntity.ok(response);
    }

    /* ADMIN */
    @Operation(summary = "Delete Visit", description = "ADMIN - Soft delete a visit")
    @DeleteMapping("/admin/{visitId}")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<Void> deleteVisit(
            @PathVariable UUID visitId) {
        visitCommandUseCase.deleteVisit(VisitId.from(visitId));
        return ResponseEntity.noContent().build();
    }

    /* ADMIN */
    @Operation(summary = "Add Members to Visit", description = "ADMIN - Add multiple members to a visit")
    @PostMapping("/admin/{visitId}/members")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<AdminVisitResponse> addMembersToVisit(
            @PathVariable UUID visitId,
            @Valid @RequestBody AdminAddVisitMembersRequest request) {
        AddVisitMembersCommand command = AdminAddVisitMembersRequest.toCommand(request);
        Visit visit = visitCommandUseCase.addMembersToVisit(VisitId.from(visitId), command);
        AdminVisitResponse response = AdminVisitResponse.from(visit);
        return ResponseEntity.ok(response);
    }

    /* ADMIN */
    @Operation(summary = "Remove Member from Visit", description = "ADMIN - Remove a member from a visit")
    @DeleteMapping("/admin/{visitId}/members/{visitMemberId}")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<Void> removeMemberFromVisit(
            @PathVariable UUID visitId,
            @PathVariable UUID visitMemberId) {
        visitCommandUseCase.removeMemberFromVisit(VisitId.from(visitId), VisitMemberId.from(visitMemberId));
        return ResponseEntity.noContent().build();
    }
}
