package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class GroupMemberId extends BaseId {
    public GroupMemberId(UUID value) {
        super(value);
    }

    public static GroupMemberId newId() {
        return new GroupMemberId(UUID.randomUUID());
    }
} 