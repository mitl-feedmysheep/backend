package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

@Getter
@SuperBuilder
public class EducationProgram extends DomainEntity<EducationProgram, EducationProgramId> {

    private final GroupId groupId;
    private final String name;
    private final String description;
    private final int totalWeeks;
    private final int graduatedCount;
}
