package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MessageJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageJpaRepository extends JpaRepository<MessageJpaEntity, UUID> {

    List<MessageJpaEntity> findAllByReceiverIdOrderByCreatedAtDesc(UUID receiverId);

    long countByReceiverIdAndIsReadFalse(UUID receiverId);

    List<MessageJpaEntity> findAllBySenderIdOrderByCreatedAtDesc(UUID senderId);
}
