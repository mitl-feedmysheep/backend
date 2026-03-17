package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.DepartmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepartmentJpaRepository extends JpaRepository<DepartmentJpaEntity, UUID> {

    List<DepartmentJpaEntity> findAllByChurchId(UUID churchId);
}
