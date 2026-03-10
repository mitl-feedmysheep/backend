package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GatheringMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PrayerJpaEntity;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import mitl.IntoTheHeaven.domain.model.GatheringMember;
import mitl.IntoTheHeaven.domain.model.GatheringMemberId;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GatheringPersistenceMapperTest {

    @Mock
    private MemberPersistenceMapper memberPersistenceMapper;

    @Mock
    private MediaPersistenceMapper mediaPersistenceMapper;

    @InjectMocks
    private GatheringPersistenceMapper mapper;

    @Test
    @DisplayName("toDomain: JPA Entity -> Domain 변환 시 모든 필드와 중첩 구조가 정확하게 매핑된다")
    void toDomain() {
        UUID gatheringId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID gmId = UUID.randomUUID();
        UUID groupMemberId = UUID.randomUUID();
        UUID prayerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        Instant now = Instant.now();
        LocalDateTime prayerCreatedAt = LocalDateTime.now();

        GroupJpaEntity groupEntity = GroupJpaEntity.builder()
                .id(groupId)
                .name("청년셀")
                .build();

        MemberJpaEntity memberEntity = MemberJpaEntity.builder().id(memberId).name("기도자").build();

        PrayerJpaEntity prayerEntity = PrayerJpaEntity.builder()
                .id(prayerId)
                .member(memberEntity)
                .prayerRequest("가족 건강")
                .description("매일 기도")
                .isAnswered(false)
                .createdAt(prayerCreatedAt)
                .build();

        GroupMemberJpaEntity groupMemberEntity = GroupMemberJpaEntity.builder()
                .id(groupMemberId)
                .group(GroupJpaEntity.builder().id(groupId).build())
                .member(memberEntity)
                .role(GroupMemberRole.MEMBER)
                .status(GroupMemberStatus.ACTIVE)
                .build();

        GatheringJpaEntity gatheringEntity = GatheringJpaEntity.builder()
                .id(gatheringId)
                .build();

        GatheringMemberJpaEntity gmEntity = GatheringMemberJpaEntity.builder()
                .id(gmId)
                .gathering(gatheringEntity)
                .groupMember(groupMemberEntity)
                .worshipAttendance(true)
                .gatheringAttendance(false)
                .goal("성경 통독")
                .story("감사한 하루")
                .prayers(List.of(prayerEntity))
                .build();

        GatheringJpaEntity entity = GatheringJpaEntity.builder()
                .id(gatheringId)
                .group(groupEntity)
                .name("주간 모임")
                .description("금요 저녁 모임")
                .date(LocalDate.of(2025, 6, 6))
                .startedAt(now)
                .endedAt(now.plusSeconds(3600))
                .place("교육관")
                .leaderComment("좋은 모임")
                .adminComment("확인 완료")
                .gatheringMembers(Set.of(gmEntity))
                .build();

        GroupMember mockGroupMember = GroupMember.builder()
                .id(GroupMemberId.from(groupMemberId))
                .groupId(GroupId.from(groupId))
                .member(Member.builder().id(MemberId.from(memberId)).name("기도자").build())
                .role(GroupMemberRole.MEMBER)
                .status(GroupMemberStatus.ACTIVE)
                .build();

        Member mockMember = Member.builder().id(MemberId.from(memberId)).name("기도자").build();

        when(memberPersistenceMapper.toGroupMemberDomain(any(GroupMemberJpaEntity.class)))
                .thenReturn(mockGroupMember);
        when(memberPersistenceMapper.toDomain(any(MemberJpaEntity.class)))
                .thenReturn(mockMember);
        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Gathering domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(gatheringId);
        assertThat(domain.getGroup()).isNotNull();
        assertThat(domain.getGroup().getId().getValue()).isEqualTo(groupId);
        assertThat(domain.getGroup().getName()).isEqualTo("청년셀");
        assertThat(domain.getName()).isEqualTo("주간 모임");
        assertThat(domain.getDescription()).isEqualTo("금요 저녁 모임");
        assertThat(domain.getDate()).isEqualTo(LocalDate.of(2025, 6, 6));
        assertThat(domain.getStartedAt()).isEqualTo(now);
        assertThat(domain.getEndedAt()).isEqualTo(now.plusSeconds(3600));
        assertThat(domain.getPlace()).isEqualTo("교육관");
        assertThat(domain.getLeaderComment()).isEqualTo("좋은 모임");
        assertThat(domain.getAdminComment()).isEqualTo("확인 완료");
        assertThat(domain.getGatheringMembers()).hasSize(1);

        GatheringMember gatheringMember = domain.getGatheringMembers().get(0);
        assertThat(gatheringMember.isWorshipAttendance()).isTrue();
        assertThat(gatheringMember.isGatheringAttendance()).isFalse();
        assertThat(gatheringMember.getGoal()).isEqualTo("성경 통독");
        assertThat(gatheringMember.getStory()).isEqualTo("감사한 하루");
        assertThat(gatheringMember.getPrayers()).hasSize(1);
    }

    @Test
    @DisplayName("toDomain: groupMember가 null인 GatheringMember는 필터링되어 제외된다")
    void toDomain_filtersNullGroupMembers() {
        UUID gatheringId = UUID.randomUUID();
        UUID gmWithGroupMemberId = UUID.randomUUID();
        UUID gmWithoutGroupMemberId = UUID.randomUUID();
        UUID groupMemberId = UUID.randomUUID();

        GatheringJpaEntity gatheringEntity = GatheringJpaEntity.builder()
                .id(gatheringId)
                .build();

        GatheringMemberJpaEntity validGm = GatheringMemberJpaEntity.builder()
                .id(gmWithGroupMemberId)
                .gathering(gatheringEntity)
                .groupMember(GroupMemberJpaEntity.builder()
                        .id(groupMemberId)
                        .group(GroupJpaEntity.builder().id(UUID.randomUUID()).build())
                        .member(MemberJpaEntity.builder().id(UUID.randomUUID()).build())
                        .build())
                .worshipAttendance(true)
                .gatheringAttendance(true)
                .prayers(List.of())
                .build();

        GatheringMemberJpaEntity invalidGm = GatheringMemberJpaEntity.builder()
                .id(gmWithoutGroupMemberId)
                .gathering(gatheringEntity)
                .groupMember(null)
                .prayers(List.of())
                .build();

        Set<GatheringMemberJpaEntity> members = new HashSet<>();
        members.add(validGm);
        members.add(invalidGm);

        GatheringJpaEntity entity = GatheringJpaEntity.builder()
                .id(gatheringId)
                .group(GroupJpaEntity.builder().id(UUID.randomUUID()).name("G").build())
                .gatheringMembers(members)
                .build();

        when(memberPersistenceMapper.toGroupMemberDomain(any(GroupMemberJpaEntity.class)))
                .thenReturn(GroupMember.builder()
                        .id(GroupMemberId.from(groupMemberId))
                        .groupId(GroupId.from(UUID.randomUUID()))
                        .build());
        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Gathering domain = mapper.toDomain(entity);

        assertThat(domain.getGatheringMembers()).hasSize(1);
    }

    @Test
    @DisplayName("toDomain: group이 null인 경우 domain의 group도 null이 된다")
    void toDomain_nullGroup() {
        GatheringJpaEntity entity = GatheringJpaEntity.builder()
                .id(UUID.randomUUID())
                .group(null)
                .gatheringMembers(Set.of())
                .build();

        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Gathering domain = mapper.toDomain(entity);

        assertThat(domain.getGroup()).isNull();
    }

    @Test
    @DisplayName("toDomain: Prayer 매핑 시 순환 참조 방지를 위해 gatheringMember가 null로 설정된다")
    void toDomain_prayerCircularReferencePrevention() {
        UUID prayerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        PrayerJpaEntity prayerEntity = PrayerJpaEntity.builder()
                .id(prayerId)
                .member(MemberJpaEntity.builder().id(memberId).build())
                .prayerRequest("기도제목")
                .isAnswered(true)
                .build();

        GatheringMemberJpaEntity gmEntity = GatheringMemberJpaEntity.builder()
                .id(UUID.randomUUID())
                .gathering(GatheringJpaEntity.builder().id(UUID.randomUUID()).build())
                .groupMember(GroupMemberJpaEntity.builder()
                        .id(UUID.randomUUID())
                        .group(GroupJpaEntity.builder().id(UUID.randomUUID()).build())
                        .member(MemberJpaEntity.builder().id(UUID.randomUUID()).build())
                        .build())
                .worshipAttendance(false)
                .gatheringAttendance(true)
                .prayers(List.of(prayerEntity))
                .build();

        GatheringJpaEntity entity = GatheringJpaEntity.builder()
                .id(UUID.randomUUID())
                .group(GroupJpaEntity.builder().id(UUID.randomUUID()).name("그룹").build())
                .gatheringMembers(Set.of(gmEntity))
                .build();

        Member mockMember = Member.builder().id(MemberId.from(memberId)).build();
        when(memberPersistenceMapper.toDomain(any(MemberJpaEntity.class))).thenReturn(mockMember);
        when(memberPersistenceMapper.toGroupMemberDomain(any(GroupMemberJpaEntity.class)))
                .thenReturn(GroupMember.builder()
                        .id(GroupMemberId.from(UUID.randomUUID()))
                        .groupId(GroupId.from(UUID.randomUUID()))
                        .build());
        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Gathering domain = mapper.toDomain(entity);

        Prayer prayer = domain.getGatheringMembers().get(0).getPrayers().get(0);
        assertThat(prayer.getGatheringMember()).isNull();
        assertThat(prayer.getMember()).isNotNull();
        assertThat(prayer.getPrayerRequest()).isEqualTo("기도제목");
        assertThat(prayer.isAnswered()).isTrue();
    }

    @Test
    @DisplayName("toDomain: Prayer의 member가 null이면 domain의 member도 null이 된다")
    void toDomain_prayerWithNullMember() {
        PrayerJpaEntity prayerEntity = PrayerJpaEntity.builder()
                .id(UUID.randomUUID())
                .member(null)
                .prayerRequest("익명 기도")
                .isAnswered(false)
                .build();

        GatheringMemberJpaEntity gmEntity = GatheringMemberJpaEntity.builder()
                .id(UUID.randomUUID())
                .gathering(GatheringJpaEntity.builder().id(UUID.randomUUID()).build())
                .groupMember(GroupMemberJpaEntity.builder()
                        .id(UUID.randomUUID())
                        .group(GroupJpaEntity.builder().id(UUID.randomUUID()).build())
                        .member(MemberJpaEntity.builder().id(UUID.randomUUID()).build())
                        .build())
                .worshipAttendance(false)
                .gatheringAttendance(false)
                .prayers(List.of(prayerEntity))
                .build();

        GatheringJpaEntity entity = GatheringJpaEntity.builder()
                .id(UUID.randomUUID())
                .group(GroupJpaEntity.builder().id(UUID.randomUUID()).name("G").build())
                .gatheringMembers(Set.of(gmEntity))
                .build();

        when(memberPersistenceMapper.toGroupMemberDomain(any(GroupMemberJpaEntity.class)))
                .thenReturn(GroupMember.builder()
                        .id(GroupMemberId.from(UUID.randomUUID()))
                        .groupId(GroupId.from(UUID.randomUUID()))
                        .build());
        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Gathering domain = mapper.toDomain(entity);

        Prayer prayer = domain.getGatheringMembers().get(0).getPrayers().get(0);
        assertThat(prayer.getMember()).isNull();
    }

    @Test
    @DisplayName("toEntity(UUID): Domain -> JPA Entity 변환 시 UUID로 GroupJpaEntity stub이 생성된다")
    void toEntity_withUUID() {
        UUID gatheringId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID gmId = UUID.randomUUID();
        UUID groupMemberDomainId = UUID.randomUUID();

        GatheringMember gatheringMember = GatheringMember.builder()
                .id(GatheringMemberId.from(gmId))
                .groupMember(GroupMember.builder()
                        .id(GroupMemberId.from(groupMemberDomainId))
                        .groupId(GroupId.from(groupId))
                        .build())
                .worshipAttendance(true)
                .gatheringAttendance(true)
                .goal("목표")
                .story("이야기")
                .prayers(List.of())
                .build();

        Gathering domain = Gathering.builder()
                .id(GatheringId.from(gatheringId))
                .name("모임")
                .description("설명")
                .date(LocalDate.of(2025, 6, 1))
                .startedAt(Instant.now())
                .place("장소")
                .leaderComment("리더코멘트")
                .adminComment("관리자코멘트")
                .gatheringMembers(List.of(gatheringMember))
                .build();

        GatheringJpaEntity entity = mapper.toEntity(domain, groupId);

        assertThat(entity.getId()).isEqualTo(gatheringId);
        assertThat(entity.getName()).isEqualTo("모임");
        assertThat(entity.getGroup().getId()).isEqualTo(groupId);
        assertThat(entity.getGatheringMembers()).hasSize(1);
    }

    @Test
    @DisplayName("toEntity(GroupJpaEntity): Domain -> JPA Entity 변환 시 GroupJpaEntity가 그대로 사용된다")
    void toEntity_withGroupEntity() {
        UUID gatheringId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();

        GroupJpaEntity groupEntity = GroupJpaEntity.builder()
                .id(groupId)
                .name("그룹엔티티")
                .build();

        Gathering domain = Gathering.builder()
                .id(GatheringId.from(gatheringId))
                .name("직접모임")
                .gatheringMembers(List.of())
                .build();

        GatheringJpaEntity entity = mapper.toEntity(domain, groupEntity);

        assertThat(entity.getGroup()).isSameAs(groupEntity);
        assertThat(entity.getGatheringMembers()).isEmpty();
    }
}
