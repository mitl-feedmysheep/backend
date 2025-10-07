package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminCreateVisitRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminUpdateVisitRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminVisitListResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.AdminVisitResponse;
import mitl.IntoTheHeaven.application.port.in.command.VisitCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateVisitCommand;
import mitl.IntoTheHeaven.application.port.in.query.VisitQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import mitl.IntoTheHeaven.global.aop.RequireChurchRole;
import mitl.IntoTheHeaven.global.security.JwtAuthenticationToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Operation(summary = "Create Visit", description = "ADMIN - Create a new visit record with members and prayers")
    @PostMapping("/admin")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<AdminVisitResponse> createVisit(
            @Valid @RequestBody AdminCreateVisitRequest request,
            @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        MemberId memberId = MemberId.from(UUID.fromString(authentication.getName()));
        ChurchId churchId = ChurchId.from(UUID.fromString(authentication.getChurchId()));
        CreateVisitCommand command = AdminCreateVisitRequest.toCommand(request, churchId, memberId);
        Visit visit = visitCommandUseCase.createVisit(command);
        AdminVisitResponse response = AdminVisitResponse.from(visit);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* ADMIN */
    @Operation(summary = "Get All Visits", description = "ADMIN - Get all visits for current church (ordered by date desc)")
    @GetMapping
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<List<AdminVisitListResponse>> getAllVisits(
            @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        ChurchId churchId = ChurchId.from(UUID.fromString(authentication.getChurchId()));
        List<Visit> visits = visitQueryUseCase.getAllVisits(churchId);
        List<AdminVisitListResponse> response = AdminVisitListResponse.from(visits);
        return ResponseEntity.ok(response);
    }

    /* ADMIN */
    @Operation(summary = "Get Visit Detail", description = "ADMIN - Get visit details by ID including members and prayers")
    @GetMapping("/{visitId}")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<AdminVisitResponse> getVisit(
            @PathVariable UUID visitId) {
        Visit visit = visitQueryUseCase.getVisitById(VisitId.from(visitId));
        AdminVisitResponse response = AdminVisitResponse.from(visit);
        return ResponseEntity.ok(response);
    }

    /* ADMIN */
    @Operation(summary = "Update Visit", description = "ADMIN - Update visit information including members and prayers")
    @PutMapping("/{visitId}")
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
    @DeleteMapping("/{visitId}")
    @RequireChurchRole(ChurchRole.ADMIN)
    public ResponseEntity<Void> deleteVisit(
            @PathVariable UUID visitId) {
        visitCommandUseCase.deleteVisit(VisitId.from(visitId));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get My Visits", description = "Get all visits where I participated (ordered by date desc)")
    @GetMapping("/my")
    public ResponseEntity<List<AdminVisitListResponse>> getMyVisits(
            @AuthenticationPrincipal JwtAuthenticationToken authentication) {
        MemberId memberId = MemberId.from(UUID.fromString(authentication.getName()));
        ChurchId churchId = ChurchId.from(UUID.fromString(authentication.getChurchId()));

        ChurchMember churchMember = churchPort.findChurchMemberByMemberIdAndChurchId(memberId, churchId);
        List<Visit> visits = visitQueryUseCase.getMyVisits(churchMember.getId());
        List<AdminVisitListResponse> response = AdminVisitListResponse.from(visits);
        return ResponseEntity.ok(response);
    }
}
