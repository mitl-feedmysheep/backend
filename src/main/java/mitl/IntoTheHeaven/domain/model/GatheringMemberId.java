package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class GatheringMemberId extends BaseId {
    private GatheringMemberId(UUID value) {
        super(value);
    }

    public static GatheringMemberId from(UUID value) {
        return new GatheringMemberId(value);
    }
} 