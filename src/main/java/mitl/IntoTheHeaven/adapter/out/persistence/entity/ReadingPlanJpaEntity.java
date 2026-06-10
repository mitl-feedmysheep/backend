package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reading_plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class ReadingPlanJpaEntity extends BaseEntity {

    /**
     * 플랜 제목
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 플랜 시작일
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * 전체 일수 (진도율 분모)
     */
    @Column(name = "total_days", nullable = false)
    private int totalDays;

    /**
     * 하루 분량 목록
     */
    @OneToMany(mappedBy = "readingPlan", cascade = CascadeType.ALL)
    @OrderBy("dayNumber ASC")
    @BatchSize(size = 100)
    @Builder.Default
    private List<ReadingPlanDayJpaEntity> days = new ArrayList<>();

    /**
     * 부서 매핑 목록
     */
    @OneToMany(mappedBy = "readingPlan", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DepartmentReadingPlanJpaEntity> departmentMappings = new ArrayList<>();
}
