package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.domain.model.GatheringMember;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class UpdateGatheringMemberResponse {

    private final UUID id;
    private final UUID memberId;
    private final String memberName;
    private final boolean worshipAttendance;
    private final boolean gatheringAttendance;
    private final String story;
    private final List<PrayerResponse> prayers;

    public static UpdateGatheringMemberResponse from(GatheringMember gatheringMember) {
        return new UpdateGatheringMemberResponse(
            gatheringMember.getId().getValue(),
            gatheringMember.getMember().getId().getValue(),
            gatheringMember.getName(),
            gatheringMember.isWorshipAttendance(),
            gatheringMember.isGatheringAttendance(),
            gatheringMember.getStory(),
            gatheringMember.getPrayers().stream()
                .map(PrayerResponse::from)
                .collect(Collectors.toList())
        );
    }
} 