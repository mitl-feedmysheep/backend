package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MasterPasswordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MasterPasswordJpaRepository extends JpaRepository<MasterPasswordJpaEntity, UUID> {

    Optional<MasterPasswordJpaEntity> findFirstBy();
}
