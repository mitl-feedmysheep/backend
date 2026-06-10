package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public class ReadingPlan extends AggregateRoot<ReadingPlan, ReadingPlanId> {

    /**
     * 플랜 제목 (예: 2026 창세기 통독)
     */
    private final String title;

    /**
     * 플랜 시작일
     */
    private final LocalDate startDate;

    /**
     * 전체 일수 — 진도율 분모
     */
    private final int totalDays;

    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    @Builder.Default
    private final List<ReadingPlanDay> days = new ArrayList<>();

    public ReadingPlan delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
