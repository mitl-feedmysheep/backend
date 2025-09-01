package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.UpdateGatheringMemberRequest;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public class UpdateGatheringMemberCommand {

    private final GatheringId gatheringId;
    private final GroupMemberId groupMemberId;
    private final boolean worshipAttendance;
    private final boolean gatheringAttendance;
    private final String goal;
    private final String story;
    private final List<PrayerUpdateCommand> prayers;

    public static UpdateGatheringMemberCommand from(GatheringId gatheringId, GroupMemberId groupMemberId, UpdateGatheringMemberRequest request) {
        List<PrayerUpdateCommand> prayerCommands = request.getPrayers() != null 
            ? request.getPrayers().stream()
                .map(PrayerUpdateCommand::from)
                .collect(Collectors.toList())
            : List.of();

        return new UpdateGatheringMemberCommand(
            gatheringId,
            groupMemberId,
            request.getWorshipAttendance(),
            request.getGatheringAttendance(),
            request.getGoal(),
            request.getStory() != null ? request.getStory() : "",
            prayerCommands
        );
    }

    @Getter
    @RequiredArgsConstructor
    public static class PrayerUpdateCommand {
        private final UUID id; // optional existing id
        private final String prayerRequest;
        private final String description;

        public static PrayerUpdateCommand from(UpdateGatheringMemberRequest.PrayerRequest request) {
            return new PrayerUpdateCommand(
                request.getId(),
                request.getPrayerRequest(),
                request.getDescription()
            );
        }
    }
} 