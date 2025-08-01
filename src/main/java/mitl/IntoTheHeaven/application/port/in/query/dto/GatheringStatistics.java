package mitl.IntoTheHeaven.application.port.in.query.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class GatheringStatistics {
    
    private final UUID gatheringId;
    private final Integer totalWorshipAttendanceCount;
    private final Integer totalGatheringAttendanceCount;
    private final Integer totalPrayerRequestCount;
}