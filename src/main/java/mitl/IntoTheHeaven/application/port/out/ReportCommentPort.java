package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.ReportComment;

import java.util.List;
import java.util.UUID;

public interface ReportCommentPort {

    ReportComment save(ReportComment reportComment);

    List<ReportComment> findAllByReportId(UUID reportId);
}
