package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class ReportComment extends DomainEntity<ReportComment, ReportCommentId> {

    private final ReportId reportId;
    private final MemberId authorId;
    private final String authorName;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;
}
