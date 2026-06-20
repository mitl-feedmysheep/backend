package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class ReadingCompletionHistory extends DomainEntity<ReadingCompletionHistory, ReadingCompletionHistoryId> {

    /** 완독이 속한 활성화 기간 */
    private final DepartmentReadingPlanId departmentReadingPlanId;

    private final ReadingPlanDayId readingPlanDayId;
    private final MemberId memberId;
    private final LocalDateTime completedAt;
    private final boolean isCompleted;
}
