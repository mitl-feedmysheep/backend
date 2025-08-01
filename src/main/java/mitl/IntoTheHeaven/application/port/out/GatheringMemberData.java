package mitl.IntoTheHeaven.application.port.out;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class GatheringMemberData {
    
    private final UUID gatheringId;
    private final boolean worshipAttendance;
    private final boolean gatheringAttendance;
    private final List<UUID> prayerIds; // prayer 개수가 아닌 ID 리스트
}