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
        
        // 2. 각 GroupMember를 모임 멤버로 변환 (초기 상태로 생성)
        List<GatheringMember> gatheringMembers = groupMembers.stream()
                .map(groupMember -> {
                    return GatheringMember.builder()
                            .id(GatheringMemberId.from(UUID.randomUUID()))
                            .groupMember(groupMember)
                            .worshipAttendance(false) // 초기값: 미참석
                            .gatheringAttendance(false) // 초기값: 미참석
                            .story("") // 초기값: 빈 문자열
                            .prayers(List.of()) // 초기값: 빈 리스트
                            .build();
                })
                .collect(Collectors.toList());

        // 3. 모임 생성 (모임 멤버들과 함께)
        Gathering gathering = Gathering.builder()
                .id(GatheringId.from(UUID.randomUUID()))
                .name(command.getName())
                .description(command.getDescription())
                .date(command.getDate())
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
                        .member(targetGatheringMember.getMember())
                        .gatheringMember(targetGatheringMember)
                        .prayerRequest(prayerCommand.getPrayerRequest())
                        .description(prayerCommand.getDescription())
                        .isAnswered(false)
                        .build())
                .collect(Collectors.toList());

        // 4. 업데이트된 GatheringMember 생성
        GatheringMember updatedGatheringMember = GatheringMember.builder()
                .id(targetGatheringMember.getId())
                .groupMember(targetGatheringMember.getGroupMember())
                .name(targetGatheringMember.getName())
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
        Gathering updatedGathering = Gathering.builder()
                .id(existingGathering.getId())
                .name(existingGathering.getName())
                .description(existingGathering.getDescription())
                .date(existingGathering.getDate())
                .startedAt(existingGathering.getStartedAt())
                .endedAt(existingGathering.getEndedAt())
                .place(existingGathering.getPlace())
                .gatheringMembers(updatedGatheringMembers)
                .build();

        gatheringPort.save(updatedGathering);

        return updatedGatheringMember;
    }
} 