package mitl.IntoTheHeaven.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReportJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.ReportPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.ReportJpaRepository;
import mitl.IntoTheHeaven.application.port.out.ReportPort;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.model.Report;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ReportPersistenceAdapter implements ReportPort {

    private final ReportJpaRepository reportJpaRepository;
    private final ReportPersistenceMapper reportPersistenceMapper;

    @Override
    public Report save(Report report) {
        ReportJpaEntity entity = reportPersistenceMapper.toEntity(report);
        ReportJpaEntity saved = reportJpaRepository.save(entity);
        return reportPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Report> findById(UUID reportId) {
        return reportJpaRepository.findById(reportId)
                .map(reportPersistenceMapper::toDomain);
    }

    @Override
    public List<Report> findAllByReporterId(UUID reporterId) {
        return reportJpaRepository.findAllByReporterIdOrderByCreatedAtDesc(reporterId)
                .stream()
                .map(reportPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Report> findAll() {
        return reportJpaRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(reportPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void updateStatus(UUID reportId, ReportStatus status) {
        reportJpaRepository.findById(reportId).ifPresent(entity -> {
            entity.changeStatus(status.name());
            reportJpaRepository.save(entity);
        });
    }
}
