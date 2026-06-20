package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class PushSubscriptionId extends BaseId {

    private PushSubscriptionId(UUID value) {
        super(value);
    }

    public static PushSubscriptionId from(UUID value) {
        return new PushSubscriptionId(value);
    }

    public static PushSubscriptionId newId() {
        return new PushSubscriptionId(UUID.randomUUID());
    }
}
