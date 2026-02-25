package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EducationPort {

    EducationProgram saveProgram(EducationProgram program);

    Optional<EducationProgram> findProgramByGroupId(UUID groupId);

    EducationProgram updateProgram(EducationProgram program);

    void incrementGraduatedCount(UUID programId);

    EducationProgress saveProgress(EducationProgress progress);

    void hardDeleteProgress(UUID progressId);

    List<EducationProgress> findProgressByGroupMemberIds(List<UUID> groupMemberIds);

    List<EducationProgress> findProgressByGatheringId(UUID gatheringId);

    void graduateGroupMember(UUID groupMemberId);

    void addGroupMember(UUID groupId, UUID memberId);
}
