package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Gathering;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class GatheringResponse {

    private final UUID id;
    private final String name;
    private final LocalDate date;
    private final String place;
    private final Integer nth;
    private final Integer totalWorshipAttendanceCount;
    private final Integer totalGatheringAttendanceCount;
    private final Integer totalPrayerRequestCount;

    public static GatheringResponse from(Gathering gathering, Integer nth, 
                                       Integer totalWorshipAttendanceCount, 
                                       Integer totalGatheringAttendanceCount, 
                                       Integer totalPrayerRequestCount) {
        return GatheringResponse.builder()
                .id(gathering.getId().getValue())
                .name(gathering.getName())
                .date(gathering.getDate())
                .place(gathering.getPlace())
                .nth(nth)
                .totalWorshipAttendanceCount(totalWorshipAttendanceCount)
                .totalGatheringAttendanceCount(totalGatheringAttendanceCount)
                .totalPrayerRequestCount(totalPrayerRequestCount)
                .build();
    }
}