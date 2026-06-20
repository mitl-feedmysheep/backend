package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public class ReadingPlanDay extends DomainEntity<ReadingPlanDay, ReadingPlanDayId> {

    private final ReadingPlanId readingPlanId;

    /**
     * 플랜 내 순서 (1-based)
     */
    private final int dayNumber;

    /**
     * 읽기 범위 (예: 창세기 1-3장)
     */
    private final String readingRange;

    private final String audioUrl;

    private final String videoUrl;

    /**
     * 요약 텍스트
     */
    private final String description;

    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    @Builder.Default
    private final List<Media> medias = new ArrayList<>();

    public ReadingPlanDay delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
