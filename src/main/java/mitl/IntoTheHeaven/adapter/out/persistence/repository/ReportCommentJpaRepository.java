package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReportCommentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportCommentJpaRepository extends JpaRepository<ReportCommentJpaEntity, UUID> {

    List<ReportCommentJpaEntity> findAllByReportIdOrderByCreatedAtAsc(UUID reportId);
}
