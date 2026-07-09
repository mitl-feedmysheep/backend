package mitl.IntoTheHeaven.adapter.in.web.dto.report;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.application.dto.ReportDetail;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.enums.ReportType;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ReportDetailResponse {

    private final UUID id;
    private final ReportType type;
    private final ReportStatus status;
    private final String content;
    private final UUID reporterId;
    private final String reporterName;
    private final LocalDateTime createdAt;
    private final List<ReportCommentResponse> comments;

    public static ReportDetailResponse from(ReportDetail detail, MemberId callerId) {
        return ReportDetailResponse.builder()
                .id(detail.getReport().getId().getValue())
                .type(detail.getReport().getType())
                .status(detail.getReport().getStatus())
                .content(detail.getReport().getContent())
                .reporterId(detail.getReport().getReporterId().getValue())
                .reporterName(detail.getReport().getReporterName())
                .createdAt(detail.getReport().getCreatedAt())
                .comments(ReportCommentResponse.from(detail.getComments(), callerId))
                .build();
    }
}
