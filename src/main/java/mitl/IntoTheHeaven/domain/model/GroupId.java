package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;
import java.util.UUID;

public class GroupId extends BaseId {
    public GroupId(UUID value) {
        super(value);
    }

    public static GroupId newId() {
        return new GroupId(UUID.randomUUID());
    }
} 