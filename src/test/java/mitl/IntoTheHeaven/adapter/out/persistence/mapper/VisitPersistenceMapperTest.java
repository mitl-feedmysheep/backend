package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.PrayerJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.VisitMemberJpaEntity;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import mitl.IntoTheHeaven.domain.model.ChurchMemberId;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;
import mitl.IntoTheHeaven.domain.model.Visit;
import mitl.IntoTheHeaven.domain.model.VisitId;
import mitl.IntoTheHeaven.domain.model.VisitMember;
import mitl.IntoTheHeaven.domain.model.VisitMemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitPersistenceMapperTest {

    @Mock
    private MemberPersistenceMapper memberPersistenceMapper;

    @Mock
    private MediaPersistenceMapper mediaPersistenceMapper;

    @InjectMocks
    private VisitPersistenceMapper mapper;

    @Test
    @DisplayName("toDomain: null 입력 시 null을 반환한다")
    void toDomain_nullInput() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toDomain: JPA Entity -> Domain 변환 시 4단계 중첩 구조가 정확하게 매핑된다")
    void toDomain() {
        UUID visitId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();
        UUID pastorId = UUID.randomUUID();
        UUID visitMemberId = UUID.randomUUID();
        UUID churchMemberId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        UUID prayerId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        MemberJpaEntity memberEntity = MemberJpaEntity.builder()
                .id(memberId)
                .name("심방대상자")
                .build();

        ChurchJpaEntity churchEntity = ChurchJpaEntity.builder().id(churchId).build();

        ChurchMemberJpaEntity churchMemberEntity = ChurchMemberJpaEntity.builder()
                .id(churchMemberId)
                .church(churchEntity)
                .member(memberEntity)
                .role(ChurchRole.MEMBER)
                .createdAt(now)
                .updatedAt(now)
                .build();

        PrayerJpaEntity prayerEntity = PrayerJpaEntity.builder()
                .id(prayerId)
                .member(memberEntity)
                .prayerRequest("건강 회복")
                .description("꾸준히 기도")
                .isAnswered(false)
                .createdAt(now)
                .build();

        ChurchMemberJpaEntity pastorEntity = ChurchMemberJpaEntity.builder()
                .id(pastorId)
                .build();

        VisitJpaEntity visitEntity = VisitJpaEntity.builder()
                .id(visitId)
                .build();

        VisitMemberJpaEntity visitMemberEntity = VisitMemberJpaEntity.builder()
                .id(visitMemberId)
                .visit(visitEntity)
                .churchMember(churchMemberEntity)
                .story("감사하는 마음")
                .prayers(List.of(prayerEntity))
                .createdAt(now)
                .build();

        VisitJpaEntity entity = VisitJpaEntity.builder()
                .id(visitId)
                .church(churchEntity)
                .pastor(pastorEntity)
                .date(LocalDate.of(2025, 7, 1))
                .startedAt(now)
                .endedAt(now.plusHours(1))
                .place("성도집")
                .expense(50000)
                .notes("첫 심방")
                .visitMembers(List.of(visitMemberEntity))
                .createdAt(now)
                .deletedAt(null)
                .build();

        Member mockMember = Member.builder()
                .id(MemberId.from(memberId))
                .name("심방대상자")
                .build();

        when(memberPersistenceMapper.toDomain(any(MemberJpaEntity.class))).thenReturn(mockMember);
        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Visit domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(visitId);
        assertThat(domain.getChurchId().getValue()).isEqualTo(churchId);
        assertThat(domain.getPastorMemberId().getValue()).isEqualTo(pastorId);
        assertThat(domain.getDate()).isEqualTo(LocalDate.of(2025, 7, 1));
        assertThat(domain.getStartedAt()).isEqualTo(now);
        assertThat(domain.getEndedAt()).isEqualTo(now.plusHours(1));
        assertThat(domain.getPlace()).isEqualTo("성도집");
        assertThat(domain.getExpense()).isEqualTo(50000);
        assertThat(domain.getNotes()).isEqualTo("첫 심방");
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getDeletedAt()).isNull();
        assertThat(domain.getVisitMembers()).hasSize(1);

        VisitMember vm = domain.getVisitMembers().get(0);
        assertThat(vm.getId().getValue()).isEqualTo(visitMemberId);
        assertThat(vm.getStory()).isEqualTo("감사하는 마음");
        assertThat(vm.getChurchMember()).isNotNull();
        assertThat(vm.getChurchMember().getRole()).isEqualTo(ChurchRole.MEMBER);
        assertThat(vm.getPrayers()).hasSize(1);

        Prayer prayer = vm.getPrayers().get(0);
        assertThat(prayer.getId().getValue()).isEqualTo(prayerId);
        assertThat(prayer.getPrayerRequest()).isEqualTo("건강 회복");
        assertThat(prayer.isAnswered()).isFalse();
    }

    @Test
    @DisplayName("toDomain: Prayer 매핑 시 순환 참조 방지를 위해 visitMember가 null로 설정된다")
    void toDomain_circularReferencePrevention() {
        UUID visitId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        MemberJpaEntity memberEntity = MemberJpaEntity.builder().id(memberId).build();
        ChurchJpaEntity churchEntity = ChurchJpaEntity.builder().id(churchId).build();

        PrayerJpaEntity prayerEntity = PrayerJpaEntity.builder()
                .id(UUID.randomUUID())
                .member(memberEntity)
                .prayerRequest("test")
                .isAnswered(false)
                .build();

        VisitMemberJpaEntity vmEntity = VisitMemberJpaEntity.builder()
                .id(UUID.randomUUID())
                .visit(VisitJpaEntity.builder().id(visitId).build())
                .churchMember(ChurchMemberJpaEntity.builder()
                        .id(UUID.randomUUID())
                        .church(churchEntity)
                        .member(memberEntity)
                        .role(ChurchRole.LEADER)
                        .build())
                .prayers(List.of(prayerEntity))
                .build();

        VisitJpaEntity entity = VisitJpaEntity.builder()
                .id(visitId)
                .church(churchEntity)
                .pastor(ChurchMemberJpaEntity.builder().id(UUID.randomUUID()).build())
                .visitMembers(List.of(vmEntity))
                .build();

        when(memberPersistenceMapper.toDomain(any(MemberJpaEntity.class)))
                .thenReturn(Member.builder().id(MemberId.from(memberId)).build());
        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Visit domain = mapper.toDomain(entity);

        Prayer prayer = domain.getVisitMembers().get(0).getPrayers().get(0);
        assertThat(prayer.getVisitMember()).isNull();
    }

    @Test
    @DisplayName("toDomain: Prayer의 member가 null이면 domain의 member도 null이 된다")
    void toDomain_prayerWithNullMember() {
        UUID visitId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();

        PrayerJpaEntity prayerEntity = PrayerJpaEntity.builder()
                .id(UUID.randomUUID())
                .member(null)
                .prayerRequest("익명기도")
                .isAnswered(false)
                .build();

        ChurchJpaEntity churchEntity = ChurchJpaEntity.builder().id(churchId).build();

        VisitMemberJpaEntity vmEntity = VisitMemberJpaEntity.builder()
                .id(UUID.randomUUID())
                .visit(VisitJpaEntity.builder().id(visitId).build())
                .churchMember(ChurchMemberJpaEntity.builder()
                        .id(UUID.randomUUID())
                        .church(churchEntity)
                        .member(MemberJpaEntity.builder().id(UUID.randomUUID()).build())
                        .role(ChurchRole.MEMBER)
                        .build())
                .prayers(List.of(prayerEntity))
                .build();

        VisitJpaEntity entity = VisitJpaEntity.builder()
                .id(visitId)
                .church(churchEntity)
                .pastor(ChurchMemberJpaEntity.builder().id(UUID.randomUUID()).build())
                .visitMembers(List.of(vmEntity))
                .build();

        when(memberPersistenceMapper.toDomain(any(MemberJpaEntity.class)))
                .thenReturn(Member.builder().id(MemberId.from(UUID.randomUUID())).build());
        when(mediaPersistenceMapper.toDomainList(any())).thenReturn(List.of());

        Visit domain = mapper.toDomain(entity);

        Prayer prayer = domain.getVisitMembers().get(0).getPrayers().get(0);
        assertThat(prayer.getMember()).isNull();
    }

    @Test
    @DisplayName("toEntity: null 입력 시 null을 반환한다")
    void toEntity_nullInput() {
        assertThat(mapper.toEntity(null, UUID.randomUUID())).isNull();
    }

    @Test
    @DisplayName("toEntity: Domain -> JPA Entity 변환 시 3단계 중첩 구조(Visit->VisitMember->Prayer)가 올바르게 생성된다")
    void toEntity() {
        UUID visitId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();
        UUID pastorMemberId = UUID.randomUUID();
        UUID vmId = UUID.randomUUID();
        UUID churchMemberId = UUID.randomUUID();
        UUID prayerId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Prayer prayer = Prayer.builder()
                .id(PrayerId.from(prayerId))
                .member(Member.builder().id(MemberId.from(memberId)).name("기도자").build())
                .prayerRequest("취업 기도")
                .description("열심히 구직중")
                .isAnswered(false)
                .createdAt(now)
                .build();

        VisitMember visitMember = VisitMember.builder()
                .id(VisitMemberId.from(vmId))
                .visitId(VisitId.from(visitId))
                .churchMemberId(ChurchMemberId.from(churchMemberId))
                .story("면접 잘 봄")
                .prayers(List.of(prayer))
                .createdAt(now)
                .build();

        Visit domain = Visit.builder()
                .id(VisitId.from(visitId))
                .churchId(ChurchId.from(churchId))
                .pastorMemberId(ChurchMemberId.from(pastorMemberId))
                .date(LocalDate.of(2025, 8, 15))
                .startedAt(now)
                .endedAt(now.plusHours(2))
                .place("교회 사무실")
                .expense(30000)
                .notes("정기 심방")
                .visitMembers(List.of(visitMember))
                .build();

        VisitJpaEntity entity = mapper.toEntity(domain, churchId);

        assertThat(entity.getId()).isEqualTo(visitId);
        assertThat(entity.getChurch().getId()).isEqualTo(churchId);
        assertThat(entity.getPastor().getId()).isEqualTo(pastorMemberId);
        assertThat(entity.getDate()).isEqualTo(LocalDate.of(2025, 8, 15));
        assertThat(entity.getStartedAt()).isEqualTo(now);
        assertThat(entity.getEndedAt()).isEqualTo(now.plusHours(2));
        assertThat(entity.getPlace()).isEqualTo("교회 사무실");
        assertThat(entity.getExpense()).isEqualTo(30000);
        assertThat(entity.getNotes()).isEqualTo("정기 심방");
        assertThat(entity.getVisitMembers()).hasSize(1);

        VisitMemberJpaEntity vmEntity = entity.getVisitMembers().get(0);
        assertThat(vmEntity.getId()).isEqualTo(vmId);
        assertThat(vmEntity.getChurchMember().getId()).isEqualTo(churchMemberId);
        assertThat(vmEntity.getStory()).isEqualTo("면접 잘 봄");
        assertThat(vmEntity.getVisit()).isSameAs(entity);
        assertThat(vmEntity.getPrayers()).hasSize(1);

        PrayerJpaEntity prayerEntity = vmEntity.getPrayers().get(0);
        assertThat(prayerEntity.getId()).isEqualTo(prayerId);
        assertThat(prayerEntity.getMember().getId()).isEqualTo(memberId);
        assertThat(prayerEntity.getPrayerRequest()).isEqualTo("취업 기도");
        assertThat(prayerEntity.getVisitMember()).isSameAs(vmEntity);
    }

    @Test
    @DisplayName("toEntity: VisitMember에 deletedAt이 있으면 엔티티에도 deletedAt이 설정된다")
    void toEntity_softDeletePropagation() {
        UUID visitId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deletedAt = now.plusDays(5);

        Prayer prayer = Prayer.builder()
                .id(PrayerId.from(UUID.randomUUID()))
                .prayerRequest("soft delete 기도")
                .isAnswered(false)
                .createdAt(now)
                .deletedAt(deletedAt)
                .build();

        VisitMember visitMember = VisitMember.builder()
                .id(VisitMemberId.from(UUID.randomUUID()))
                .visitId(VisitId.from(visitId))
                .churchMemberId(ChurchMemberId.from(UUID.randomUUID()))
                .story("삭제된 멤버")
                .prayers(List.of(prayer))
                .createdAt(now)
                .deletedAt(deletedAt)
                .build();

        Visit domain = Visit.builder()
                .id(VisitId.from(visitId))
                .churchId(ChurchId.from(churchId))
                .pastorMemberId(ChurchMemberId.from(UUID.randomUUID()))
                .date(LocalDate.now())
                .visitMembers(List.of(visitMember))
                .build();

        VisitJpaEntity entity = mapper.toEntity(domain, churchId);

        VisitMemberJpaEntity vmEntity = entity.getVisitMembers().get(0);
        assertThat(vmEntity.getDeletedAt()).isEqualTo(deletedAt);

        PrayerJpaEntity prayerEntity = vmEntity.getPrayers().get(0);
        assertThat(prayerEntity.getDeletedAt()).isEqualTo(deletedAt);
    }

    @Test
    @DisplayName("toEntity: VisitMember에 deletedAt이 null이면 엔티티의 deletedAt도 null이다")
    void toEntity_noSoftDelete() {
        UUID visitId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        VisitMember visitMember = VisitMember.builder()
                .id(VisitMemberId.from(UUID.randomUUID()))
                .visitId(VisitId.from(visitId))
                .churchMemberId(ChurchMemberId.from(UUID.randomUUID()))
                .prayers(List.of())
                .createdAt(now)
                .deletedAt(null)
                .build();

        Visit domain = Visit.builder()
                .id(VisitId.from(visitId))
                .churchId(ChurchId.from(churchId))
                .pastorMemberId(ChurchMemberId.from(UUID.randomUUID()))
                .date(LocalDate.now())
                .visitMembers(List.of(visitMember))
                .build();

        VisitJpaEntity entity = mapper.toEntity(domain, churchId);

        VisitMemberJpaEntity vmEntity = entity.getVisitMembers().get(0);
        assertThat(vmEntity.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("toEntity: Prayer의 member가 null이면 PrayerJpaEntity의 member도 null이다")
    void toEntity_prayerWithNullMember() {
        UUID visitId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Prayer prayer = Prayer.builder()
                .id(PrayerId.from(UUID.randomUUID()))
                .member(null)
                .prayerRequest("멤버 없는 기도")
                .isAnswered(false)
                .createdAt(now)
                .build();

        VisitMember visitMember = VisitMember.builder()
                .id(VisitMemberId.from(UUID.randomUUID()))
                .visitId(VisitId.from(visitId))
                .churchMemberId(ChurchMemberId.from(UUID.randomUUID()))
                .prayers(List.of(prayer))
                .createdAt(now)
                .build();

        Visit domain = Visit.builder()
                .id(VisitId.from(visitId))
                .churchId(ChurchId.from(churchId))
                .pastorMemberId(ChurchMemberId.from(UUID.randomUUID()))
                .date(LocalDate.now())
                .visitMembers(List.of(visitMember))
                .build();

        VisitJpaEntity entity = mapper.toEntity(domain, churchId);

        PrayerJpaEntity prayerEntity = entity.getVisitMembers().get(0).getPrayers().get(0);
        assertThat(prayerEntity.getMember()).isNull();
    }
}
