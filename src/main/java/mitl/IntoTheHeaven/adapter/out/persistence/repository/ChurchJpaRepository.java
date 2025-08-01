package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ChurchJpaRepository extends JpaRepository<ChurchJpaEntity, UUID> {
}