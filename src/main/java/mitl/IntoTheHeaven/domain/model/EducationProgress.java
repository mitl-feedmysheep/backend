package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDate;

@Getter
@SuperBuilder
public class EducationProgress extends DomainEntity<EducationProgress, EducationProgressId> {

    private final GroupMemberId groupMemberId;
    private final GatheringId gatheringId;
    private final int weekNumber;
    private final LocalDate completedDate;
}
