package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.model.Report;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportPort {

    Report save(Report report);

    Optional<Report> findById(UUID reportId);

    List<Report> findAllByReporterId(UUID reporterId);

    List<Report> findAll();

    void updateStatus(UUID reportId, ReportStatus status);
}
