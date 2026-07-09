package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import mitl.IntoTheHeaven.adapter.in.web.dto.report.CreateReportRequest;
import mitl.IntoTheHeaven.domain.enums.ReportType;
import mitl.IntoTheHeaven.domain.model.MemberId;

@Data
@AllArgsConstructor
public class CreateReportCommand {

    private MemberId reporterId;
    private ReportType type;
    private String content;

    public static CreateReportCommand from(CreateReportRequest request, MemberId reporterId) {
        return new CreateReportCommand(
                reporterId,
                request.getType(),
                request.getContent()
        );
    }
}
