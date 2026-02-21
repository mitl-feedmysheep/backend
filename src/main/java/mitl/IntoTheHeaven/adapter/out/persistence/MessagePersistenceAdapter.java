package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MessageJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.MessagePersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.MessageJpaRepository;
import mitl.IntoTheHeaven.application.port.out.MessagePort;
import mitl.IntoTheHeaven.domain.model.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessagePersistenceAdapter implements MessagePort {

    private final MessageJpaRepository messageJpaRepository;
    private final MessagePersistenceMapper messagePersistenceMapper;

    @Override
    public Message save(Message message) {
        MessageJpaEntity entity = messagePersistenceMapper.toEntity(message);
        MessageJpaEntity saved = messageJpaRepository.save(entity);
        return messagePersistenceMapper.toDomain(saved);
    }

    @Override
    public List<Message> findByReceiverId(UUID receiverId) {
        return messageJpaRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(messagePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByReceiverId(UUID receiverId) {
        return messageJpaRepository.countByReceiverIdAndIsReadFalse(receiverId);
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return messageJpaRepository.findById(messageId)
                .map(messagePersistenceMapper::toDomain);
    }

    @Override
    public void markAsRead(UUID messageId) {
        messageJpaRepository.findById(messageId).ifPresent(entity -> {
            entity.markAsRead();
            messageJpaRepository.save(entity);
        });
    }

    @Override
    public List<Message> findBySenderId(UUID senderId) {
        return messageJpaRepository.findAllBySenderIdOrderByCreatedAtDesc(senderId)
                .stream()
                .map(messagePersistenceMapper::toDomain)
                .toList();
    }
}
