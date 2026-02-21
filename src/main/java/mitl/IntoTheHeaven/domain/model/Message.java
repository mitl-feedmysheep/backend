package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.MessageType;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class Message extends DomainEntity<Message, MessageId> {

    private final MemberId senderId;
    private final String senderName;
    private final MemberId receiverId;
    private final String receiverName;
    private final String content;
    private final MessageType type;
    private final boolean isRead;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    public Message markAsRead() {
        return this.toBuilder()
                .isRead(true)
                .build();
    }
}
