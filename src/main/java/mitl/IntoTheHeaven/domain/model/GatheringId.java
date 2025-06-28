package mitl.IntoTheHeaven.domain.model;

import lombok.NoArgsConstructor;
import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

@NoArgsConstructor
public class GatheringId extends BaseId {

    public GatheringId(UUID id) {
        super(id);
    }

    public static GatheringId newId() {
        return new GatheringId(UUID.randomUUID());
    }
} 