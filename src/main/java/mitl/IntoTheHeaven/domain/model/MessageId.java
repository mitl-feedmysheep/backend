package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.global.domain.BaseId;

import java.util.UUID;

public class MessageId extends BaseId {
    private MessageId(UUID value) {
        super(value);
    }

    public static MessageId from(UUID value) {
        return new MessageId(value);
    }
}
