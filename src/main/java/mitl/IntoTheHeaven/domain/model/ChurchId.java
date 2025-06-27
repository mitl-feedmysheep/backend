package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ChurchId extends BaseId {
    public ChurchId(UUID value) {
        super(value);
    }

    public static ChurchId newId() {
        return new ChurchId(UUID.randomUUID());
    }
} 