package mitl.IntoTheHeaven.domain.model;

import java.util.UUID;
import mitl.IntoTheHeaven.global.domain.BaseId;

public class MemberId extends BaseId {

    private MemberId(UUID value) {
        super(value);
    }

    public static MemberId from(UUID value) {
        return new MemberId(value);
    }
} 