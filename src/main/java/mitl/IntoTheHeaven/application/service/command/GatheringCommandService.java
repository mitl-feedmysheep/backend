package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.GatheringCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.CreateGatheringCommand;
import mitl.IntoTheHeaven.application.port.in.command.dto.UpdateGatheringMemberCommand;
import mitl.IntoTheHeaven.application.port.out.GatheringPort;
import mitl.IntoTheHeaven.application.port.out.MemberPort;
import mitl.IntoTheHeaven.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GatheringCommandService implements GatheringCommandUseCase {

    private final GatheringPort gatheringPort;
    private final MemberPort memberPort;

    @Override
    public Gathering createGathering(CreateGatheringCommand command) {
        // 1. 해당 그룹의 모든 GroupMember들을 조회
        List<GroupMember> groupMembers = memberPort.findGroupMembersByGroupId(command.getGroupId().getValue());

        // 2. 모임 ID 생성
        GatheringId gatheringId = GatheringId.from(UUID.randomUUID());
        
        // 3. 각 GroupMember를 모임 멤버로 변환
        List<GatheringMember> gatheringMembers = groupMembers.stream()
                .map(groupMember -> GatheringMember.builder()
                        .id(GatheringMemberId.from(UUID.randomUUID()))
                        .gatheringId(gatheringId) // 단방향 참조: ID만 사용
                        .groupMember(groupMember)
                        .worshipAttendance(false)
                        .gatheringAttendance(false)
                        .story(null)
                        .prayers(List.of())
                        .build())
                .collect(Collectors.toList());

        // 4. 모임 생성 (멤버들과 함께)
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
        // 1. 기존 모임 조회
        Gathering existingGathering = gatheringPort.findDetailById(command.getGatheringId().getValue())
                .orElseThrow(() -> new RuntimeException("Gathering not found"));

        // 2. 업데이트할 GatheringMember 찾기
        GatheringMember targetGatheringMember = existingGathering.getGatheringMembers().stream()
                .filter(gm -> gm.getGroupMember().getId().equals(command.getGroupMemberId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Gathering member not found"));

        // 3. 새로운 기도제목들 생성
        List<Prayer> updatedPrayers = command.getPrayers().stream()
                .map(prayerCommand -> Prayer.builder()
                        .id(PrayerId.from(UUID.randomUUID()))
                        .member(null) // persistence layer에서 처리
                        .gatheringMember(targetGatheringMember)
                        .prayerRequest(prayerCommand.getPrayerRequest())
                        .description(prayerCommand.getDescription())
                        .isAnswered(false)
                        .build())
                .collect(Collectors.toList());

        // 4. 업데이트된 GatheringMember 생성
        GatheringMember updatedGatheringMember = GatheringMember.builder()
                .id(targetGatheringMember.getId())
                .gatheringId(targetGatheringMember.getGatheringId()) // ID만 사용
                .groupMember(targetGatheringMember.getGroupMember())
                .worshipAttendance(command.isWorshipAttendance())
                .gatheringAttendance(command.isGatheringAttendance())
                .story(command.getStory())
                .prayers(updatedPrayers)
                .build();

        // 5. 전체 GatheringMember 리스트에서 해당 멤버만 교체
        List<GatheringMember> updatedGatheringMembers = existingGathering.getGatheringMembers().stream()
                .map(gm -> gm.getGroupMember().getId().equals(command.getGroupMemberId()) 
                    ? updatedGatheringMember 
                    : gm)
                .collect(Collectors.toList());

        // 6. 업데이트된 모임 생성 및 저장
        Gathering updatedGathering = existingGathering.toBuilder()
                .gatheringMembers(updatedGatheringMembers)
                .build();

        gatheringPort.save(updatedGathering);

        return updatedGatheringMember;
    }
} 