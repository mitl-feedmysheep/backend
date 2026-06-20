package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ReadingPlanDayId extends BaseId {
    private ReadingPlanDayId(UUID value) {
        super(value);
    }

    public static ReadingPlanDayId from(UUID value) {
        return new ReadingPlanDayId(value);
    }
}
