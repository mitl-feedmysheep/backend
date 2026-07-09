package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.application.dto.ReportDetail;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Report;
import mitl.IntoTheHeaven.domain.model.ReportId;

import java.util.List;

public interface ReportQueryUseCase {

    List<Report> getReports(MemberId callerId, ReportStatus statusFilter);

    ReportDetail getReportDetail(MemberId callerId, ReportId reportId);
}
