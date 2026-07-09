package mitl.IntoTheHeaven.adapter.in.web.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import mitl.IntoTheHeaven.domain.enums.ReportType;

@Getter
@Setter
public class CreateReportRequest {

    @NotNull(message = "Type is required")
    private ReportType type;

    @NotBlank(message = "Content is required")
    @Size(max = 2000, message = "Content must be less than 2000 characters")
    private String content;
}
