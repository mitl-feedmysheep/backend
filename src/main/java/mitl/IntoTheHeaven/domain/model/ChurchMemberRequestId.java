package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ChurchMemberRequestId extends BaseId {
    private ChurchMemberRequestId(UUID value) {
        super(value);
    }

    public static ChurchMemberRequestId from(UUID value) {
        return new ChurchMemberRequestId(value);
    }
}
