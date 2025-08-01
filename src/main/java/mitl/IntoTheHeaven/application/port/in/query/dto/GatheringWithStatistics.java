package mitl.IntoTheHeaven.application.port.in.query.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Gathering;

@Getter
@Builder
public class GatheringWithStatistics {
    
    private final Gathering gathering;
    private final Integer nth;
    private final Integer totalWorshipAttendanceCount;
    private final Integer totalGatheringAttendanceCount;
    private final Integer totalPrayerRequestCount;
}