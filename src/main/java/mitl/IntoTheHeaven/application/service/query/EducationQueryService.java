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
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EducationQueryService implements EducationQueryUseCase {

    private final EducationPort educationPort;
    private final GroupPort groupPort;

    @Override
    public Optional<EducationProgramWithProgress> getProgramWithProgress(GroupId groupId) {
        Optional<EducationProgram> programOpt = educationPort.findProgramByGroupId(groupId.getValue());
        if (programOpt.isEmpty()) {
            return Optional.empty();
        }

        EducationProgram program = programOpt.get();
        List<GroupMember> groupMembers = groupPort.findAllGroupMembersByGroupId(groupId.getValue());

        List<UUID> groupMemberIds = groupMembers.stream()
                .map(gm -> gm.getId().getValue())
                .toList();

        List<EducationProgress> progressList = groupMemberIds.isEmpty()
                ? Collections.emptyList()
                : educationPort.findProgressByGroupMemberIds(groupMemberIds);

        return Optional.of(EducationProgramWithProgress.builder()
                .program(program)
                .progressList(progressList)
                .build());
    }

    @Override
    public List<EducationProgress> getProgressByGathering(GatheringId gatheringId) {
        return educationPort.findProgressByGatheringId(gatheringId.getValue());
    }
}
