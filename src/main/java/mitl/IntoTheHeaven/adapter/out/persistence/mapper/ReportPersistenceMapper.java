package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReportJpaEntity;
import mitl.IntoTheHeaven.domain.enums.ReportStatus;
import mitl.IntoTheHeaven.domain.enums.ReportType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Report;
import mitl.IntoTheHeaven.domain.model.ReportId;
import org.springframework.stereotype.Component;

@Component
public class ReportPersistenceMapper {

    public Report toDomain(ReportJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Report.builder()
                .id(ReportId.from(entity.getId()))
                .reporterId(MemberId.from(entity.getReporter().getId()))
                .reporterName(entity.getReporter().getName())
                .type(ReportType.valueOf(entity.getType()))
                .content(entity.getContent())
                .status(ReportStatus.valueOf(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public ReportJpaEntity toEntity(Report domain) {
        if (domain == null) {
            return null;
        }
        return ReportJpaEntity.builder()
                .id(domain.getId().getValue())
                .reporter(MemberJpaEntity.builder().id(domain.getReporterId().getValue()).build())
                .type(domain.getType().name())
                .content(domain.getContent())
                .status(domain.getStatus().name())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
