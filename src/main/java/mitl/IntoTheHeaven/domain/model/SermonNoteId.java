package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class SermonNoteId extends BaseId {
    private SermonNoteId(UUID value) {
        super(value);
    }

    public static SermonNoteId from(UUID value) {
        return new SermonNoteId(value);
    }
}
