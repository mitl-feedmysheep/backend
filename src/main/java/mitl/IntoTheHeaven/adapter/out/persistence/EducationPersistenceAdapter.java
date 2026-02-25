package mitl.IntoTheHeaven.adapter.out.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.EducationProgramJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.EducationProgressJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.mapper.EducationPersistenceMapper;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.EducationProgramJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.EducationProgressJpaRepository;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.GroupMemberJpaRepository;
import mitl.IntoTheHeaven.application.port.out.EducationPort;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.model.EducationProgram;
import mitl.IntoTheHeaven.domain.model.EducationProgress;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QEducationProgramJpaEntity.educationProgramJpaEntity;
import static mitl.IntoTheHeaven.adapter.out.persistence.entity.QGroupMemberJpaEntity.groupMemberJpaEntity;

@Component
@RequiredArgsConstructor
public class EducationPersistenceAdapter implements EducationPort {

    private final EducationProgramJpaRepository programRepository;
    private final EducationProgressJpaRepository progressRepository;
    private final GroupMemberJpaRepository groupMemberRepository;
    private final EducationPersistenceMapper mapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public EducationProgram saveProgram(EducationProgram program) {
        EducationProgramJpaEntity entity = mapper.toProgramEntity(program);
        EducationProgramJpaEntity saved = programRepository.save(entity);
        return mapper.toProgramDomain(saved);
    }

    @Override
    public Optional<EducationProgram> findProgramByGroupId(UUID groupId) {
        return programRepository.findByGroupId(groupId)
                .map(mapper::toProgramDomain);
    }

    @Override
    public EducationProgram updateProgram(EducationProgram program) {
        EducationProgramJpaEntity entity = mapper.toProgramEntity(program);
        EducationProgramJpaEntity saved = programRepository.save(entity);
        return mapper.toProgramDomain(saved);
    }

    @Override
    public void incrementGraduatedCount(UUID programId) {
        queryFactory
                .update(educationProgramJpaEntity)
                .set(educationProgramJpaEntity.graduatedCount,
                        educationProgramJpaEntity.graduatedCount.add(1))
                .where(educationProgramJpaEntity.id.eq(programId))
                .execute();
    }

    @Override
    public EducationProgress saveProgress(EducationProgress progress) {
        EducationProgressJpaEntity entity = mapper.toProgressEntity(progress);
        EducationProgressJpaEntity saved = progressRepository.save(entity);
        return mapper.toProgressDomain(saved);
    }

    @Override
    public void hardDeleteProgress(UUID progressId) {
        progressRepository.deleteById(progressId);
    }

    @Override
    public List<EducationProgress> findProgressByGroupMemberIds(List<UUID> groupMemberIds) {
        return progressRepository.findByGroupMemberIdIn(groupMemberIds).stream()
                .map(mapper::toProgressDomain)
                .toList();
    }

    @Override
    public List<EducationProgress> findProgressByGatheringId(UUID gatheringId) {
        return progressRepository.findByGatheringId(gatheringId).stream()
                .map(mapper::toProgressDomain)
                .toList();
    }

    @Override
    public void softDeleteGroupMember(UUID groupMemberId) {
        queryFactory
                .update(groupMemberJpaEntity)
                .set(groupMemberJpaEntity.deletedAt, LocalDateTime.now())
                .where(groupMemberJpaEntity.id.eq(groupMemberId))
                .execute();
    }

    @Override
    public void addGroupMember(UUID groupId, UUID memberId) {
        GroupMemberJpaEntity newMember = GroupMemberJpaEntity.builder()
                .id(UUID.randomUUID())
                .group(GroupJpaEntity.builder().id(groupId).build())
                .member(MemberJpaEntity.builder().id(memberId).build())
                .role(GroupMemberRole.MEMBER)
                .build();
        groupMemberRepository.save(newMember);
    }
}
