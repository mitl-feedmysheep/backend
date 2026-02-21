package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessagePort {

    Message save(Message message);

    List<Message> findByReceiverId(UUID receiverId);

    long countUnreadByReceiverId(UUID receiverId);

    Optional<Message> findById(UUID messageId);

    void markAsRead(UUID messageId);

    List<Message> findBySenderId(UUID senderId);
}
