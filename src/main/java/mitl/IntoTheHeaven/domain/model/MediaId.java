package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;
import java.util.UUID;

public class MediaId extends BaseId {
    private MediaId(UUID value) {
        super(value);
    }

    public static MediaId from(UUID value) {
        return new MediaId(value);
    }
}
