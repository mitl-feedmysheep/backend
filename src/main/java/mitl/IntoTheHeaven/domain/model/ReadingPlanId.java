package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ReadingPlanId extends BaseId {
    private ReadingPlanId(UUID value) {
        super(value);
    }

    public static ReadingPlanId from(UUID value) {
        return new ReadingPlanId(value);
    }
}
