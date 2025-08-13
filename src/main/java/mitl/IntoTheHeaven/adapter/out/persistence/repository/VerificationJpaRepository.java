package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.VerificationJpaEntity;
import mitl.IntoTheHeaven.domain.enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationJpaRepository extends JpaRepository<VerificationJpaEntity, UUID> {
    Optional<VerificationJpaEntity> findTopByTypeAndTypeValueOrderByCreatedAtDesc(VerificationType type, String typeValue);
}


