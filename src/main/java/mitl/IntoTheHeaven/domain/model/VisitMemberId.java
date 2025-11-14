package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class VisitMemberId extends BaseId {

    private VisitMemberId(UUID value) {
        super(value);
    }

    public static VisitMemberId from(UUID value) {
        return new VisitMemberId(value);
    }
}

