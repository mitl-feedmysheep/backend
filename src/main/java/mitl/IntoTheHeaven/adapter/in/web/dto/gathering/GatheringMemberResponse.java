package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Builder;
import lombok.Data;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import mitl.IntoTheHeaven.domain.model.GatheringMember;
import mitl.IntoTheHeaven.domain.model.Prayer;
import java.util.UUID;
import java.time.LocalDate;

@Data
@Builder
public class GatheringMemberResponse {

    private UUID gatheringMemberId;
    private UUID groupMemberId;
    private UUID memberId;
    private String name;
    private LocalDate birthday;
    private boolean worshipAttendance;
    private boolean gatheringAttendance;
    private String goal;
    private String story;
    private List<PrayerResponse> prayers;

    public static GatheringMemberResponse from(GatheringMember gatheringMember) {
        return GatheringMemberResponse.builder()
                .gatheringMemberId(gatheringMember.getId().getValue())
                .groupMemberId(gatheringMember.getGroupMember().getId().getValue())
                .memberId(gatheringMember.getGroupMember().getMember().getId().getValue())
                .name(gatheringMember.getGroupMember().getMember().getName())
                .birthday(gatheringMember.getGroupMember().getMember().getBirthday())
                .worshipAttendance(gatheringMember.isWorshipAttendance())
                .gatheringAttendance(gatheringMember.isGatheringAttendance())
                .goal(gatheringMember.getGoal())
                .story(gatheringMember.getStory())
                .prayers(gatheringMember.getPrayers().stream()
                        .sorted(Comparator.comparing(Prayer::getCreatedAt))
                        .map(PrayerResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
} 