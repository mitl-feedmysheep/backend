package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reading_plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class ReadingPlanJpaEntity extends BaseEntity {

    @Column(name = "church_id", nullable = false, columnDefinition = "CHAR(36)")
    private UUID churchId;

    /**
     * 플랜 제목
     */
    @Column(nullable = false, length = 100)
    private String title;

    /**
     * 읽기 요일 비트마스크 (bit0=월, bit1=화, ..., bit6=일, 기본값 63=월~토)
     */
    @Column(name = "reading_days", nullable = false)
    private int readingDays;

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
