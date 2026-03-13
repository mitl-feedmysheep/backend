package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;
import java.util.UUID;

public class DepartmentId extends BaseId {
    private DepartmentId(UUID value) {
        super(value);
    }

    public static DepartmentId from(UUID value) {
        return new DepartmentId(value);
    }
}
