package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;
import java.util.UUID;

public class GroupId extends BaseId {
    private GroupId(UUID value) {
        super(value);
    }

    public static GroupId from(UUID value) {
        return new GroupId(value);
    }
} 