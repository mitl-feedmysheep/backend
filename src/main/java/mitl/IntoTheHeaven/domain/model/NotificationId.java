package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class NotificationId extends BaseId {
    private NotificationId(UUID value) {
        super(value);
    }

    public static NotificationId from(UUID value) {
        return new NotificationId(value);
    }
}
