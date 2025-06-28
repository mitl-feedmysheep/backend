package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class PrayerId extends BaseId {
    private PrayerId(UUID value) {
        super(value);
    }

    public static PrayerId from(UUID value) {
        return new PrayerId(value);
    }
} 