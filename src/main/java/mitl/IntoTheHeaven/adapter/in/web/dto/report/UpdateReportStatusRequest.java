package mitl.IntoTheHeaven.adapter.in.web.dto.report;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;

@Getter
@Setter
public class UpdateReportStatusRequest {

    @NotNull(message = "Status is required")
    private ReportStatus status;
}
