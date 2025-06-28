package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class GatheringId extends BaseId {

    private GatheringId(UUID value) {
        super(value);
    }

    public static GatheringId from(UUID value) {
        return new GatheringId(value);
    }
} 