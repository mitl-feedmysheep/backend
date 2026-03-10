package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EducationPersistenceMapperTest {

    private final EducationPersistenceMapper mapper = new EducationPersistenceMapper();

    // --- EducationProgram ---

    @Test
    @DisplayName("toProgramDomain: null 입력 시 null을 반환한다")
    void toProgramDomain_nullInput() {
        assertThat(mapper.toProgramDomain(null)).isNull();
    }

    @Test
    @DisplayName("toProgramDomain: JPA Entity -> Domain 변환 시 모든 필드가 정확하게 매핑된다")
    void toProgramDomain() {
        UUID id = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        EducationProgramJpaEntity entity = EducationProgramJpaEntity.builder()
                .id(id)
                .group(GroupJpaEntity.builder().id(groupId).build())
                .name("새가족 교육")
                .description("12주 과정")
                .totalWeeks(12)
                .graduatedCount(5)
                .build();

        EducationProgram domain = mapper.toProgramDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getGroupId().getValue()).isEqualTo(groupId);
        assertThat(domain.getName()).isEqualTo("새가족 교육");
        assertThat(domain.getDescription()).isEqualTo("12주 과정");
        assertThat(domain.getTotalWeeks()).isEqualTo(12);
        assertThat(domain.getGraduatedCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("toProgramEntity: null 입력 시 null을 반환한다")
    void toProgramEntity_nullInput() {
        assertThat(mapper.toProgramEntity(null)).isNull();
    }

    @Test
    @DisplayName("toProgramEntity: Domain -> JPA Entity 변환 시 stub GroupJpaEntity가 올바르게 생성된다")
    void toProgramEntity() {
        UUID id = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        EducationProgram domain = EducationProgram.builder()
                .id(EducationProgramId.from(id))
                .groupId(GroupId.from(groupId))
                .name("양육 훈련")
                .description("8주 양육")
                .totalWeeks(8)
                .graduatedCount(3)
                .build();

        EducationProgramJpaEntity entity = mapper.toProgramEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getGroup().getId()).isEqualTo(groupId);
        assertThat(entity.getName()).isEqualTo("양육 훈련");
        assertThat(entity.getDescription()).isEqualTo("8주 양육");
        assertThat(entity.getTotalWeeks()).isEqualTo(8);
        assertThat(entity.getGraduatedCount()).isEqualTo(3);
    }

    // --- EducationProgress ---

    @Test
    @DisplayName("toProgressDomain: null 입력 시 null을 반환한다")
    void toProgressDomain_nullInput() {
        assertThat(mapper.toProgressDomain(null)).isNull();
    }

    @Test
    @DisplayName("toProgressDomain: JPA Entity -> Domain 변환 시 모든 필드가 정확하게 매핑된다")
    void toProgressDomain() {
        UUID id = UUID.randomUUID();
        UUID groupMemberId = UUID.randomUUID();
        UUID gatheringId = UUID.randomUUID();
        LocalDate completedDate = LocalDate.of(2025, 5, 20);

        EducationProgressJpaEntity entity = EducationProgressJpaEntity.builder()
                .id(id)
                .groupMember(GroupMemberJpaEntity.builder().id(groupMemberId).build())
                .gathering(GatheringJpaEntity.builder().id(gatheringId).build())
                .weekNumber(3)
                .completedDate(completedDate)
                .build();

        EducationProgress domain = mapper.toProgressDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getGroupMemberId().getValue()).isEqualTo(groupMemberId);
        assertThat(domain.getGatheringId().getValue()).isEqualTo(gatheringId);
        assertThat(domain.getWeekNumber()).isEqualTo(3);
        assertThat(domain.getCompletedDate()).isEqualTo(completedDate);
    }

    @Test
    @DisplayName("toProgressEntity: null 입력 시 null을 반환한다")
    void toProgressEntity_nullInput() {
        assertThat(mapper.toProgressEntity(null)).isNull();
    }

    @Test
    @DisplayName("toProgressEntity: Domain -> JPA Entity 변환 시 stub 엔티티들이 올바르게 생성된다")
    void toProgressEntity() {
        UUID id = UUID.randomUUID();
        UUID groupMemberId = UUID.randomUUID();
        UUID gatheringId = UUID.randomUUID();
        LocalDate completedDate = LocalDate.of(2025, 6, 10);

        EducationProgress domain = EducationProgress.builder()
                .id(EducationProgressId.from(id))
                .groupMemberId(GroupMemberId.from(groupMemberId))
                .gatheringId(GatheringId.from(gatheringId))
                .weekNumber(7)
                .completedDate(completedDate)
                .build();

        EducationProgressJpaEntity entity = mapper.toProgressEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getGroupMember().getId()).isEqualTo(groupMemberId);
        assertThat(entity.getGathering().getId()).isEqualTo(gatheringId);
        assertThat(entity.getWeekNumber()).isEqualTo(7);
        assertThat(entity.getCompletedDate()).isEqualTo(completedDate);
    }
}
