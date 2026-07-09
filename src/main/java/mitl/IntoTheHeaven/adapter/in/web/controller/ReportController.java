package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.report.AddReportCommentRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.report.CreateReportRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.report.ReportDetailResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.report.ReportResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.report.UpdateReportStatusRequest;
import mitl.IntoTheHeaven.application.dto.ReportDetail;
import mitl.IntoTheHeaven.application.port.in.command.ReportCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateReportCommand;
import mitl.IntoTheHeaven.application.port.in.query.ReportQueryUseCase;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Report;
import mitl.IntoTheHeaven.domain.model.ReportId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "Report", description = "APIs for bug report / feature request submissions")
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportCommandUseCase reportCommandUseCase;
    private final ReportQueryUseCase reportQueryUseCase;

    @Operation(summary = "Create Report", description = "Submits a new bug report / feature request / question.")
    @PostMapping
    public ResponseEntity<Map<String, UUID>> create(
            @AuthenticationPrincipal String memberId,
            @Valid @RequestBody CreateReportRequest request) {
        MemberId reporterId = MemberId.from(UUID.fromString(memberId));
        CreateReportCommand command = CreateReportCommand.from(request, reporterId);
        ReportId reportId = reportCommandUseCase.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", reportId.getValue()));
    }

    @Operation(summary = "Get Reports", description = "Returns the caller's own reports, or every report if the caller is the system admin.")
    @GetMapping
    public ResponseEntity<List<ReportResponse>> getReports(
            @AuthenticationPrincipal String memberId,
            @RequestParam(required = false) ReportStatus status) {
        List<Report> reports = reportQueryUseCase.getReports(MemberId.from(UUID.fromString(memberId)), status);
        return ResponseEntity.ok(ReportResponse.from(reports));
    }

    @Operation(summary = "Get Report Detail", description = "Returns a report with its comment thread. Allowed for the reporter or the system admin.")
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDetailResponse> getReportDetail(
            @AuthenticationPrincipal String memberId,
            @PathVariable UUID reportId) {
        MemberId callerId = MemberId.from(UUID.fromString(memberId));
        ReportDetail detail = reportQueryUseCase.getReportDetail(callerId, ReportId.from(reportId));
        return ResponseEntity.ok(ReportDetailResponse.from(detail, callerId));
    }

    @Operation(summary = "Add Comment", description = "Adds a comment to the report thread. Allowed for the reporter or the system admin.")
    @PostMapping("/{reportId}/comments")
    public ResponseEntity<Void> addComment(
            @AuthenticationPrincipal String memberId,
            @PathVariable UUID reportId,
            @Valid @RequestBody AddReportCommentRequest request) {
        reportCommandUseCase.addComment(
                MemberId.from(UUID.fromString(memberId)),
                ReportId.from(reportId),
                request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Update Report Status", description = "Changes the report's status. System admin only. Does not trigger a notification.")
    @PatchMapping("/{reportId}/status")
    public ResponseEntity<Void> updateStatus(
            @AuthenticationPrincipal String memberId,
            @PathVariable UUID reportId,
            @Valid @RequestBody UpdateReportStatusRequest request) {
        reportCommandUseCase.updateStatus(
                MemberId.from(UUID.fromString(memberId)),
                ReportId.from(reportId),
                request.getStatus());
        return ResponseEntity.noContent().build();
    }
}
