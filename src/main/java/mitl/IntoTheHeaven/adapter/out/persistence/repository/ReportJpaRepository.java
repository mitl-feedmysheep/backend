package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReportJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportJpaEntity, UUID> {

    List<ReportJpaEntity> findAllByReporterIdOrderByCreatedAtDesc(UUID reporterId);

    List<ReportJpaEntity> findAllByOrderByCreatedAtDesc();
}
