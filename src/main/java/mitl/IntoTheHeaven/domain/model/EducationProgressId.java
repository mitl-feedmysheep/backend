package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class EducationProgressId extends BaseId {
    private EducationProgressId(UUID value) {
        super(value);
    }

    public static EducationProgressId from(UUID value) {
        return new EducationProgressId(value);
    }
}
