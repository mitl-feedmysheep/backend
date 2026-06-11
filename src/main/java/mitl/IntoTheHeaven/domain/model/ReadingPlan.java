package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.AggregateRoot;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public class ReadingPlan extends AggregateRoot<ReadingPlan, ReadingPlanId> {

    private final UUID churchId;

    /**
     * 플랜 제목 (예: 2026 창세기 통독)
     */
    private final String title;

    /**
     * 읽기 요일 비트마스크 (bit0=월, bit1=화, ..., bit6=일, 기본값 63=월~토)
     */
    private final int readingDays;

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
