package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class VisitId extends BaseId {

    private VisitId(UUID value) {
        super(value);
    }

    public static VisitId from(UUID value) {
        return new VisitId(value);
    }
}

