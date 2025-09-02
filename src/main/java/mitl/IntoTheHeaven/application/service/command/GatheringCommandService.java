package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.GatheringCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateGatheringCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringMemberCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringCommand;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GatheringCommandService implements GatheringCommandUseCase {

    private final GatheringPort gatheringPort;
    private final MemberPort memberPort;

    @Override
    public Gathering createGathering(CreateGatheringCommand command) {
        // 1. Retrieve all GroupMembers for the group
        List<GroupMember> groupMembers = memberPort.findGroupMembersByGroupId(command.getGroupId().getValue());

        // 2. Generate gathering ID
        GatheringId gatheringId = GatheringId.from(UUID.randomUUID());
        
        // 3. Convert each GroupMember to GatheringMember
        List<GatheringMember> gatheringMembers = groupMembers.stream()
                .map(groupMember -> GatheringMember.builder()
                        .id(GatheringMemberId.from(UUID.randomUUID()))
                        .gatheringId(gatheringId) // Unidirectional reference: use ID only
                        .groupMember(groupMember)
                        .worshipAttendance(false)
                        .gatheringAttendance(false)
                        .goal(null)
                        .story(null)
                        .prayers(List.of())
                        .build())
                .collect(Collectors.toList());

        // 4. Create gathering with members
        Gathering gathering = Gathering.builder()
                .id(gatheringId)
                .group(Group.builder().id(command.getGroupId()).build())
                .name(command.getName())
                .date(command.getDate())
                .description(command.getDescription())
                .startedAt(command.getStartedAt())
                .endedAt(command.getEndedAt())
                .place(command.getPlace())
                .gatheringMembers(gatheringMembers)
                .build();

        return gatheringPort.save(gathering, command.getGroupId().getValue());
    }

    @Override
    public GatheringMember updateGatheringMember(UpdateGatheringMemberCommand command) {
        // 1. Retrieve existing gathering
        Gathering existingGathering = gatheringPort.findDetailById(command.getGatheringId().getValue())
                .orElseThrow(() -> new RuntimeException("Gathering not found"));

        // 2. Find GatheringMember to update
        GatheringMember targetGatheringMember = existingGathering.getGatheringMembers().stream()
                .filter(gm -> gm.getGroupMember().getId().equals(command.getGroupMemberId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Gathering member not found"));

        // 3. Build upserted prayer list (full sync)
        Map<UUID, Prayer> existingPrayerById = targetGatheringMember.getPrayers().stream()
                .collect(Collectors.toMap(p -> p.getId().getValue(), Function.identity()));

        List<Prayer> mergedPrayers = new ArrayList<>();
        for (UpdateGatheringMemberCommand.PrayerUpdateCommand pCmd : command.getPrayers()) {
            if (pCmd.getId() != null && existingPrayerById.containsKey(pCmd.getId())) {
                // Update existing: preserve id and createdAt, keep isAnswered as is
                Prayer prev = existingPrayerById.get(pCmd.getId());
                Prayer updated = Prayer.builder()
                        .id(prev.getId())
                        .member(targetGatheringMember.getGroupMember().getMember())
                        .gatheringMember(targetGatheringMember)
                        .prayerRequest(pCmd.getPrayerRequest())
                        .description(pCmd.getDescription())
                        .isAnswered(prev.isAnswered())
                        .build();
                mergedPrayers.add(updated);
            } else {
                // Create new
                Prayer created = Prayer.builder()
                        .id(PrayerId.from(UUID.randomUUID()))
                        .member(targetGatheringMember.getGroupMember().getMember())
                        .gatheringMember(targetGatheringMember)
                        .prayerRequest(pCmd.getPrayerRequest())
                        .description(pCmd.getDescription())
                        .isAnswered(false)
                        .build();
                mergedPrayers.add(created);
            }
        }

        // 4. Create updated GatheringMember (missing old prayers are removed via orphanRemoval)
        GatheringMember updatedGatheringMember = GatheringMember.builder()
                .id(targetGatheringMember.getId())
                .gatheringId(targetGatheringMember.getGatheringId()) // Unidirectional reference: use ID only
                .groupMember(targetGatheringMember.getGroupMember())
                .worshipAttendance(command.isWorshipAttendance())
                .gatheringAttendance(command.isGatheringAttendance())
                .goal(command.getGoal())
                .story(command.getStory())
                .prayers(mergedPrayers)
                .build();

        // 5. Replace only the target member in the GatheringMember list
        List<GatheringMember> updatedGatheringMembers = existingGathering.getGatheringMembers().stream()
                .map(gm -> gm.getGroupMember().getId().equals(command.getGroupMemberId()) 
                    ? updatedGatheringMember 
                    : gm)
                .collect(Collectors.toList());

        // 6. Create and save updated gathering
        Gathering updatedGathering = existingGathering.toBuilder()
                .gatheringMembers(updatedGatheringMembers)
                .build();

        gatheringPort.save(updatedGathering);

        return updatedGatheringMember;
    }

    @Override
    public Gathering updateGathering(UpdateGatheringCommand command) {
        Gathering existingGathering = gatheringPort.findDetailById(command.getGatheringId().getValue())
                .orElseThrow(() -> new RuntimeException("Gathering not found"));

        Gathering updated = existingGathering.toBuilder()
                .name(command.getName() != null ? command.getName() : existingGathering.getName())
                .description(command.getDescription() != null ? command.getDescription() : existingGathering.getDescription())
                .date(command.getDate() != null ? command.getDate() : existingGathering.getDate())
                .startedAt(command.getStartedAt() != null ? command.getStartedAt() : existingGathering.getStartedAt())
                .endedAt(command.getEndedAt() != null ? command.getEndedAt() : existingGathering.getEndedAt())
                .place(command.getPlace() != null ? command.getPlace() : existingGathering.getPlace())
                .leaderComment(command.getLeaderComment() != null ? command.getLeaderComment() : existingGathering.getLeaderComment())
                .adminComment(command.getAdminComment() != null ? command.getAdminComment() : existingGathering.getAdminComment())
                .build();

        Gathering saved = gatheringPort.save(updated);
        return saved;
    }
} 