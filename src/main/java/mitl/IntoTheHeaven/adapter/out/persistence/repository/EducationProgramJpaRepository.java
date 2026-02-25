package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.EducationProgramJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EducationProgramJpaRepository extends JpaRepository<EducationProgramJpaEntity, UUID> {

    Optional<EducationProgramJpaEntity> findByGroupId(UUID groupId);
}
