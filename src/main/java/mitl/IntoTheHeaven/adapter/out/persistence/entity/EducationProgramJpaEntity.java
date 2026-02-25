package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "education_program")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class EducationProgramJpaEntity extends BaseEntity {

    /**
     * Associated group (1:1 relationship)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, unique = true)
    private GroupJpaEntity group;

    /**
     * Education program name
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Education program description
     */
    @Column(length = 500)
    private String description;

    /**
     * Total number of education weeks
     */
    @Column(name = "total_weeks", nullable = false)
    private int totalWeeks;

    /**
     * Count of members who have graduated
     */
    @Column(name = "graduated_count", nullable = false)
    @Builder.Default
    private int graduatedCount = 0;
}
