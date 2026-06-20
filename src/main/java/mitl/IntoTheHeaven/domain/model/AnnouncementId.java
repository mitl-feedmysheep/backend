package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class AnnouncementId extends BaseId {
    private AnnouncementId(UUID value) {
        super(value);
    }

    public static AnnouncementId from(UUID value) {
        return new AnnouncementId(value);
    }
}
