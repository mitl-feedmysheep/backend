package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.dto.ReportDetail;
import mitl.IntoTheHeaven.application.port.in.query.ReportQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.ReportCommentPort;
import mitl.IntoTheHeaven.application.port.out.ReportPort;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Report;
import mitl.IntoTheHeaven.domain.model.ReportId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportQueryService implements ReportQueryUseCase {

    private final ReportPort reportPort;
    private final ReportCommentPort reportCommentPort;

    @Value("${report.system-admin-member-id}")
    private String systemAdminMemberId;

    @Override
    public List<Report> getReports(MemberId callerId, ReportStatus statusFilter) {
        List<Report> reports = isSystemAdmin(callerId)
                ? reportPort.findAll()
                : reportPort.findAllByReporterId(callerId.getValue());

        if (statusFilter == null) {
            return reports;
        }
        return reports.stream()
                .filter(report -> report.getStatus() == statusFilter)
                .toList();
    }

    @Override
    public ReportDetail getReportDetail(MemberId callerId, ReportId reportId) {
        Report report = reportPort.findById(reportId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        boolean callerIsReporter = report.getReporterId().equals(callerId);
        if (!callerIsReporter && !isSystemAdmin(callerId)) {
            throw new AccessDeniedException("Only the reporter or admin can view this report");
        }

        return ReportDetail.builder()
                .report(report)
                .comments(reportCommentPort.findAllByReportId(reportId.getValue()))
                .build();
    }

    private boolean isSystemAdmin(MemberId memberId) {
        return memberId.getValue().toString().equals(systemAdminMemberId);
    }
}
