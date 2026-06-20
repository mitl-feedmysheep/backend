package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reading_plan_day",
        indexes = @Index(name = "idx_reading_plan_day_plan_num", columnList = "reading_plan_id, day_number"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@SQLRestriction("deleted_at is null")
public class ReadingPlanDayJpaEntity extends BaseEntity {

    /**
     * 상위 플랜
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reading_plan_id", nullable = false)
    private ReadingPlanJpaEntity readingPlan;

    /**
     * 플랜 내 순서 (1-based)
     */
    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    /**
     * 읽기 범위 (예: 창세기 1-3장)
     */
    @Column(name = "reading_range", nullable = false, length = 200)
    private String readingRange;

    @Column(name = "audio_url", length = 500)
    private String audioUrl;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    /**
     * 요약 텍스트
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 요약 사진 (Media 다형성 패턴)
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @SQLRestriction("entity_type = 'READING_DAY'")
    @OrderBy("createdAt ASC")
    @BatchSize(size = 5)
    @Builder.Default
    private List<MediaJpaEntity> medias = new ArrayList<>();
}
