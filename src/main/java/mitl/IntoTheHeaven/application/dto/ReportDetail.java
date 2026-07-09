package mitl.IntoTheHeaven.application.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Report;
import mitl.IntoTheHeaven.domain.model.ReportComment;

import java.util.List;

@Getter
@Builder
public class ReportDetail {

    private final Report report;
    private final List<ReportComment> comments;
    private final List<String> mediaUrls;
}
