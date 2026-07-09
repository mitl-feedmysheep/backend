package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.CreateReportCommand;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.ReportId;

public interface ReportCommandUseCase {

    ReportId create(CreateReportCommand command);

    void addComment(MemberId callerId, ReportId reportId, String content);

    void updateStatus(MemberId callerId, ReportId reportId, ReportStatus status);
}
