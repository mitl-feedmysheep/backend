package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;

import java.time.LocalDate;

/**
 * Education progress record - uses hard delete (no soft-delete)
 * to avoid unique constraint conflicts on (group_member_id, week_number).
 */
@Entity
@Table(name = "education_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"group_member_id", "week_number"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class EducationProgressJpaEntity extends BaseEntity {

    /**
     * Group member who completed this education week
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_member_id", nullable = false)
    private GroupMemberJpaEntity groupMember;

    /**
     * Gathering in which this education week was completed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_id", nullable = false)
    private GatheringJpaEntity gathering;

    /**
     * Education week number (1-based)
     */
    @Column(name = "week_number", nullable = false)
    private int weekNumber;

    /**
     * Date when this week was completed
     */
    @Column(name = "completed_date", nullable = false)
    private LocalDate completedDate;
}
