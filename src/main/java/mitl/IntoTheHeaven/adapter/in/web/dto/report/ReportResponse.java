package mitl.IntoTheHeaven.adapter.in.web.dto.report;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.enums.ReportType;
import mitl.IntoTheHeaven.domain.model.Report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ReportResponse {

    private final UUID id;
    private final ReportType type;
    private final ReportStatus status;
    private final String content;
    private final String reporterName;
    private final String reporterAffiliation;
    private final LocalDateTime createdAt;

    public static ReportResponse from(Report report) {
        return ReportResponse.builder()
                .id(report.getId().getValue())
                .type(report.getType())
                .status(report.getStatus())
                .content(report.getContent())
                .reporterName(report.getReporterName())
                .reporterAffiliation(report.getReporterAffiliation())
                .createdAt(report.getCreatedAt())
                .build();
    }

    public static List<ReportResponse> from(List<Report> reports) {
        return reports.stream()
                .map(ReportResponse::from)
                .toList();
    }
}
