package mitl.IntoTheHeaven.domain.model;

import java.util.UUID;
import mitl.IntoTheHeaven.global.domain.BaseId;

public class EventId extends BaseId {

    private EventId(UUID value) {
        super(value);
    }

    public static EventId from(UUID value) {
        return new EventId(value);
    }
}
