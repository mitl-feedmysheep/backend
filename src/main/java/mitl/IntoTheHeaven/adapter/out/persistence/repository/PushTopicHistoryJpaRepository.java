package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.PushTopicHistoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PushTopicHistoryJpaRepository extends JpaRepository<PushTopicHistoryJpaEntity, UUID> {
}
