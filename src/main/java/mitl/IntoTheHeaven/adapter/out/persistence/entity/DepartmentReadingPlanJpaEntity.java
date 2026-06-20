package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Entity
@Table(name = "department_reading_plan",
        indexes = {
                @Index(name = "idx_dept_reading_plan_dept", columnList = "department_id"),
                @Index(name = "idx_dept_reading_plan_plan", columnList = "reading_plan_id")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class DepartmentReadingPlanJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private DepartmentJpaEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_plan_id", nullable = false)
    private ReadingPlanJpaEntity readingPlan;

    /** 운영 시작일 */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** 운영 종료일 */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
}
