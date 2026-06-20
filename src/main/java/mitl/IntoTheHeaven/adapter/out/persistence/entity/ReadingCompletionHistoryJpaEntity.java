package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * 멤버 완독 기록 이력 — 활성화 기간(DepartmentReadingPlan) 단위로 구분.
 * unmark 시 hard delete. deleted_at 컬럼은 DDL 일관성 용도이며 실제 사용하지 않음.
 */
@Entity
@Table(name = "reading_completion_history",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_completion_activation_day_member",
                columnNames = {"department_reading_plan_id", "reading_plan_day_id", "member_id"}
        ),
        indexes = @Index(name = "idx_reading_completion_history_member", columnList = "member_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class ReadingCompletionHistoryJpaEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_reading_plan_id", nullable = false)
    private DepartmentReadingPlanJpaEntity departmentReadingPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_plan_day_id", nullable = false)
    private ReadingPlanDayJpaEntity readingPlanDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberJpaEntity member;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;
}
