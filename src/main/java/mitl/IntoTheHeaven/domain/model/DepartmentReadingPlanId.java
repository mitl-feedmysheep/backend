package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class DepartmentReadingPlanId extends BaseId {
    private DepartmentReadingPlanId(UUID value) {
        super(value);
    }

    public static DepartmentReadingPlanId from(UUID value) {
        return new DepartmentReadingPlanId(value);
    }
}
