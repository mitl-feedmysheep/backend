package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ChurchId extends BaseId {
    private ChurchId(UUID value) {
        super(value);
    }

    public static ChurchId from(UUID value) {
        return new ChurchId(value);
    }
} 