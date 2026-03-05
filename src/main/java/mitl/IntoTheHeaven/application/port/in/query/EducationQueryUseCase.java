package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.application.port.in.query.dto.EducationProgramWithProgress;
import mitl.IntoTheHeaven.domain.model.EducationProgress;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupId;

import java.util.List;
import java.util.Optional;

public interface EducationQueryUseCase {

    Optional<EducationProgramWithProgress> getProgramWithProgress(GroupId groupId);

    List<EducationProgress> getProgressByGathering(GatheringId gatheringId);
}
