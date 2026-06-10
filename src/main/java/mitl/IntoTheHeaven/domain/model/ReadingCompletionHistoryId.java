package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class ReadingCompletionHistoryId extends BaseId {

    private ReadingCompletionHistoryId(UUID value) {
        super(value);
    }

    public static ReadingCompletionHistoryId from(UUID value) {
        return new ReadingCompletionHistoryId(value);
    }
}
