package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;
import java.util.UUID;

public class DepartmentMemberId extends BaseId {
    private DepartmentMemberId(UUID value) {
        super(value);
    }

    public static DepartmentMemberId from(UUID value) {
        return new DepartmentMemberId(value);
    }
}
