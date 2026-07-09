package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.enums.ReportType;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class Report extends DomainEntity<Report, ReportId> {

    private final MemberId reporterId;
    private final String reporterName;
    private final String reporterAffiliation;
    private final ReportType type;
    private final String content;
    private final ReportStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;

    public Report changeStatus(ReportStatus newStatus) {
        return this.toBuilder()
                .status(newStatus)
                .build();
    }

    public Report withReporterAffiliation(String affiliation) {
        return this.toBuilder()
                .reporterAffiliation(affiliation)
                .build();
    }
}
