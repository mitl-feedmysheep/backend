package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class DepartmentReadingPlan extends DomainEntity<DepartmentReadingPlan, DepartmentReadingPlanId> {

    private final DepartmentId departmentId;
    private final ReadingPlanId readingPlanId;
    private final ReadingPlan readingPlan;

    /** 운영 시작일 */
    private final LocalDate startDate;

    /** 운영 종료일 */
    private final LocalDate endDate;

    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    public DepartmentReadingPlan delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
