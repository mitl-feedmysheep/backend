package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class EducationProgramId extends BaseId {
    private EducationProgramId(UUID value) {
        super(value);
    }

    public static EducationProgramId from(UUID value) {
        return new EducationProgramId(value);
    }
}
