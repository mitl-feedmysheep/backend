package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.dto.ReportDetail;
import mitl.IntoTheHeaven.application.port.in.query.ReportQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.ChurchPort;
import mitl.IntoTheHeaven.application.port.out.DepartmentPort;
import mitl.IntoTheHeaven.application.port.out.MediaPort;
import mitl.IntoTheHeaven.application.port.out.ReportCommentPort;
import mitl.IntoTheHeaven.application.port.out.ReportPort;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.Department;
import mitl.IntoTheHeaven.domain.model.DepartmentMember;
import mitl.IntoTheHeaven.domain.model.Media;
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
    private final MediaPort mediaPort;
    private final DepartmentPort departmentPort;
    private final ChurchPort churchPort;

    @Value("${report.system-admin-member-id}")
    private String systemAdminMemberId;

    @Override
    public List<Report> getReports(MemberId callerId, ReportStatus statusFilter) {
        boolean callerIsAdmin = isSystemAdmin(callerId);
        List<Report> reports = callerIsAdmin
                ? reportPort.findAll()
                : reportPort.findAllByReporterId(callerId.getValue());

        if (statusFilter != null) {
            reports = reports.stream()
                    .filter(report -> report.getStatus() == statusFilter)
                    .toList();
        }

        if (!callerIsAdmin) {
            return reports;
        }
        return reports.stream()
                .map(report -> report.withReporterAffiliation(resolveAffiliation(report.getReporterId())))
                .toList();
    }

    @Override
    public ReportDetail getReportDetail(MemberId callerId, ReportId reportId) {
        Report report = reportPort.findById(reportId.getValue())
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));

        boolean callerIsReporter = report.getReporterId().equals(callerId);
        boolean callerIsAdmin = isSystemAdmin(callerId);
        if (!callerIsReporter && !callerIsAdmin) {
            throw new AccessDeniedException("Only the reporter or admin can view this report");
        }

        if (callerIsAdmin) {
            report = report.withReporterAffiliation(resolveAffiliation(report.getReporterId()));
        }

        List<Media> medias = mediaPort.findByEntity(EntityType.REPORT, reportId.getValue());
        List<String> mediaUrls = medias.stream().map(Media::getUrl).toList();

        return ReportDetail.builder()
                .report(report)
                .comments(reportCommentPort.findAllByReportId(reportId.getValue()))
                .mediaUrls(mediaUrls)
                .build();
    }

    private boolean isSystemAdmin(MemberId memberId) {
        return memberId.getValue().toString().equals(systemAdminMemberId);
    }

    private String resolveAffiliation(MemberId reporterId) {
        List<DepartmentMember> departmentMembers = departmentPort.findDepartmentMembersByMemberId(reporterId.getValue());
        if (departmentMembers.isEmpty()) {
            return null;
        }
        DepartmentMember departmentMember = departmentMembers.stream()
                .filter(dm -> dm.getStatus() == DepartmentMemberStatus.ACTIVE)
                .findFirst()
                .orElse(departmentMembers.get(0));

        Department department = departmentPort.findById(departmentMember.getDepartmentId().getValue()).orElse(null);
        if (department == null) {
            return null;
        }

        Church church = churchPort.findById(department.getChurchId().getValue());
        String churchName = church != null ? church.getName() : null;
        return churchName != null ? churchName + ", " + department.getName() : department.getName();
    }
}
