package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReportCommentJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ReportJpaEntity;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.ReportComment;
import mitl.IntoTheHeaven.domain.model.ReportCommentId;
import mitl.IntoTheHeaven.domain.model.ReportId;
import org.springframework.stereotype.Component;

@Component
public class ReportCommentPersistenceMapper {

    public ReportComment toDomain(ReportCommentJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return ReportComment.builder()
                .id(ReportCommentId.from(entity.getId()))
                .reportId(ReportId.from(entity.getReport().getId()))
                .authorId(MemberId.from(entity.getAuthor().getId()))
                .authorName(entity.getAuthor().getName())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    public ReportCommentJpaEntity toEntity(ReportComment domain) {
        if (domain == null) {
            return null;
        }
        return ReportCommentJpaEntity.builder()
                .id(domain.getId().getValue())
                .report(ReportJpaEntity.builder().id(domain.getReportId().getValue()).build())
                .author(MemberJpaEntity.builder().id(domain.getAuthorId().getValue()).build())
                .content(domain.getContent())
                .deletedAt(domain.getDeletedAt())
                .build();
    }
}
