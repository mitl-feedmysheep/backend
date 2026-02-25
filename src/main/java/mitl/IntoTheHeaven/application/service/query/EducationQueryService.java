package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.EducationQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.dto.EducationProgramWithProgress;
import mitl.IntoTheHeaven.application.port.out.EducationPort;
import mitl.IntoTheHeaven.application.port.out.GroupPort;
import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgress;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EducationQueryService implements EducationQueryUseCase {

    private final EducationPort educationPort;
    private final GroupPort groupPort;

    @Override
    public EducationProgramWithProgress getProgramWithProgress(GroupId groupId) {
        EducationProgram program = educationPort.findProgramByGroupId(groupId.getValue())
                .orElseThrow(() -> new RuntimeException(
                        "Education program not found for group: " + groupId.getValue()));

        List<GroupMember> groupMembers = groupPort.findGroupMembersByGroupId(groupId.getValue());

        List<UUID> groupMemberIds = groupMembers.stream()
                .map(gm -> gm.getId().getValue())
                .toList();

        List<EducationProgress> progressList = groupMemberIds.isEmpty()
                ? Collections.emptyList()
                : educationPort.findProgressByGroupMemberIds(groupMemberIds);

        return EducationProgramWithProgress.builder()
                .program(program)
                .progressList(progressList)
                .build();
    }

    @Override
    public List<EducationProgress> getProgressByGathering(GatheringId gatheringId) {
        return educationPort.findProgressByGatheringId(gatheringId.getValue());
    }
}
