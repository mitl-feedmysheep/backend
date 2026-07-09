package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReportCommentJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ReportCommentPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReportCommentJpaRepository;
import mitl.IntoTheHeaven.application.port.out.ReportCommentPort;
import mitl.IntoTheHeaven.domain.model.ReportComment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReportCommentPersistenceAdapter implements ReportCommentPort {

    private final ReportCommentJpaRepository reportCommentJpaRepository;
    private final ReportCommentPersistenceMapper reportCommentPersistenceMapper;

    @Override
    public ReportComment save(ReportComment reportComment) {
        ReportCommentJpaEntity entity = reportCommentPersistenceMapper.toEntity(reportComment);
        ReportCommentJpaEntity saved = reportCommentJpaRepository.save(entity);
        return reportCommentPersistenceMapper.toDomain(saved);
    }

    @Override
    public List<ReportComment> findAllByReportId(UUID reportId) {
        return reportCommentJpaRepository.findAllByReportIdOrderByCreatedAtAsc(reportId)
                .stream()
                .map(reportCommentPersistenceMapper::toDomain)
                .toList();
    }
}
