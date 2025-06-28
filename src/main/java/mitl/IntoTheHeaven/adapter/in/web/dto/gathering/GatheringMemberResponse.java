package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;
import mitl.IntoTheHeaven.domain.model.GatheringMember;
import java.util.UUID;

@Data
@Builder
public class GatheringMemberResponse {

    private UUID memberId;
    private String name;
    private boolean worshipAttendance;
    private boolean gatheringAttendance;
    private String story;
    private List<PrayerResponse> prayers;

    public static GatheringMemberResponse from(GatheringMember gatheringMember) {
        return GatheringMemberResponse.builder()
                .memberId(gatheringMember.getMember().getId().getValue())
                .name(gatheringMember.getName())
                .worshipAttendance(gatheringMember.isWorshipAttendance())
                .gatheringAttendance(gatheringMember.isGatheringAttendance())
                .story(gatheringMember.getStory())
                .prayers(gatheringMember.getPrayers().stream()
                        .map(PrayerResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
} 