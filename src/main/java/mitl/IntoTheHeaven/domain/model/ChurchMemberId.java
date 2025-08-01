package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ChurchMemberId extends BaseId {
    private ChurchMemberId(UUID value) {
        super(value);
    }

    public static ChurchMemberId from(UUID value) {
        return new ChurchMemberId(value);
    }
}