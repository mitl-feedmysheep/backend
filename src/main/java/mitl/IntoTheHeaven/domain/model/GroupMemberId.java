package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class GroupMemberId extends BaseId {
    private GroupMemberId(UUID value) {
        super(value);
    }

    public static GroupMemberId from(UUID value) {
        return new GroupMemberId(value);
    }
} 