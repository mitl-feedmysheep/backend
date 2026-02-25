package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.EducationProgramJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.EducationProgressJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgramId;
import mitl.IntoTheHeaven.domain.model.EducationProgress;
import mitl.IntoTheHeaven.domain.model.EducationProgressId;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EducationPersistenceMapper {

    public EducationProgram toProgramDomain(EducationProgramJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return EducationProgram.builder()
                .id(EducationProgramId.from(entity.getId()))
                .groupId(GroupId.from(entity.getGroup().getId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .totalWeeks(entity.getTotalWeeks())
                .graduatedCount(entity.getGraduatedCount())
                .build();
    }

    public EducationProgramJpaEntity toProgramEntity(EducationProgram domain) {
        if (domain == null) {
            return null;
        }
        return EducationProgramJpaEntity.builder()
                .id(domain.getId().getValue())
                .group(GroupJpaEntity.builder().id(domain.getGroupId().getValue()).build())
                .name(domain.getName())
                .description(domain.getDescription())
                .totalWeeks(domain.getTotalWeeks())
                .graduatedCount(domain.getGraduatedCount())
                .build();
    }

    public EducationProgress toProgressDomain(EducationProgressJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return EducationProgress.builder()
                .id(EducationProgressId.from(entity.getId()))
                .groupMemberId(GroupMemberId.from(entity.getGroupMember().getId()))
                .gatheringId(GatheringId.from(entity.getGathering().getId()))
                .weekNumber(entity.getWeekNumber())
                .completedDate(entity.getCompletedDate())
                .build();
    }

    public EducationProgressJpaEntity toProgressEntity(EducationProgress domain) {
        if (domain == null) {
            return null;
        }
        return EducationProgressJpaEntity.builder()
                .id(domain.getId().getValue())
                .groupMember(GroupMemberJpaEntity.builder().id(domain.getGroupMemberId().getValue()).build())
                .gathering(GatheringJpaEntity.builder().id(domain.getGatheringId().getValue()).build())
                .weekNumber(domain.getWeekNumber())
                .completedDate(domain.getCompletedDate())
                .build();
    }
}
