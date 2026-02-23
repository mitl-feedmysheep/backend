package mitl.IntoTheHeaven.domain.model;

import java.util.UUID;
import mitl.IntoTheHeaven.global.domain.BaseId;

public class MasterPasswordId extends BaseId {

    private MasterPasswordId(UUID value) {
        super(value);
    }

    public static MasterPasswordId from(UUID value) {
        return new MasterPasswordId(value);
    }
}
