package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.GroupMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.domain.enums.BaptismStatus;
import mitl.IntoTheHeaven.domain.enums.GroupMemberRole;
import mitl.IntoTheHeaven.domain.enums.GroupMemberStatus;
import mitl.IntoTheHeaven.domain.enums.Sex;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
import mitl.IntoTheHeaven.domain.model.GroupMemberId;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MemberPersistenceMapperTest {

    private final MemberPersistenceMapper mapper = new MemberPersistenceMapper();

    @Test
    @DisplayName("toDomain: null 입력 시 null을 반환한다")
    void toDomain_nullInput() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toDomain: JPA Entity -> Domain 변환 시 모든 필드가 정확하게 매핑된다")
    void toDomain() {
        UUID id = UUID.randomUUID();
        LocalDate birthday = LocalDate.of(1995, 3, 15);
        LocalDateTime now = LocalDateTime.now();

        MemberJpaEntity entity = MemberJpaEntity.builder()
                .id(id)
                .name("홍길동")
                .email("hong@example.com")
                .password("encodedPassword")
                .sex(Sex.M)
                .birthday(birthday)
                .phone("010-1234-5678")
                .profileUrl("https://cdn.example.com/profile.jpg")
                .address("서울시 강남구")
                .description("테스트 회원")
                .occupation("개발자")
                .baptismStatus(BaptismStatus.BAPTIZED)
                .mbti("INTJ")
                .isProvisioned(true)
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .deletedAt(now.plusDays(1))
                .build();

        Member domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("홍길동");
        assertThat(domain.getEmail()).isEqualTo("hong@example.com");
        assertThat(domain.getPassword()).isEqualTo("encodedPassword");
        assertThat(domain.getSex()).isEqualTo(Sex.M);
        assertThat(domain.getBirthday()).isEqualTo(birthday);
        assertThat(domain.getPhone()).isEqualTo("010-1234-5678");
        assertThat(domain.getProfileUrl()).isEqualTo("https://cdn.example.com/profile.jpg");
        assertThat(domain.getAddress()).isEqualTo("서울시 강남구");
        assertThat(domain.getDescription()).isEqualTo("테스트 회원");
        assertThat(domain.getOccupation()).isEqualTo("개발자");
        assertThat(domain.getBaptismStatus()).isEqualTo(BaptismStatus.BAPTIZED);
        assertThat(domain.getMbti()).isEqualTo("INTJ");
        assertThat(domain.getIsProvisioned()).isTrue();
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getUpdatedAt()).isEqualTo(now.plusHours(1));
        assertThat(domain.getDeletedAt()).isEqualTo(now.plusDays(1));
    }

    @Test
    @DisplayName("toDomain: Sex.F 및 BaptismStatus.NOT_BAPTIZED 열거형이 정확히 보존된다")
    void toDomain_enumMapping() {
        MemberJpaEntity entity = MemberJpaEntity.builder()
                .id(UUID.randomUUID())
                .name("김영희")
                .sex(Sex.F)
                .baptismStatus(BaptismStatus.NOT_BAPTIZED)
                .build();

        Member domain = mapper.toDomain(entity);

        assertThat(domain.getSex()).isEqualTo(Sex.F);
        assertThat(domain.getBaptismStatus()).isEqualTo(BaptismStatus.NOT_BAPTIZED);
    }

    @Test
    @DisplayName("toEntity: null 입력 시 null을 반환한다")
    void toEntity_nullInput() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toEntity: Domain -> JPA Entity 변환 시 createdAt/updatedAt/deletedAt은 포함하지 않는다")
    void toEntity() {
        UUID id = UUID.randomUUID();
        LocalDate birthday = LocalDate.of(1990, 7, 20);

        Member domain = Member.builder()
                .id(MemberId.from(id))
                .name("이철수")
                .email("lee@example.com")
                .password("hashedPw")
                .sex(Sex.M)
                .birthday(birthday)
                .phone("010-9876-5432")
                .profileUrl("https://cdn.example.com/lee.jpg")
                .address("부산시")
                .description("설명")
                .occupation("디자이너")
                .baptismStatus(BaptismStatus.PAEDOBAPTISM)
                .mbti("ENFP")
                .isProvisioned(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        MemberJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("이철수");
        assertThat(entity.getEmail()).isEqualTo("lee@example.com");
        assertThat(entity.getPassword()).isEqualTo("hashedPw");
        assertThat(entity.getSex()).isEqualTo(Sex.M);
        assertThat(entity.getBirthday()).isEqualTo(birthday);
        assertThat(entity.getPhone()).isEqualTo("010-9876-5432");
        assertThat(entity.getProfileUrl()).isEqualTo("https://cdn.example.com/lee.jpg");
        assertThat(entity.getAddress()).isEqualTo("부산시");
        assertThat(entity.getDescription()).isEqualTo("설명");
        assertThat(entity.getOccupation()).isEqualTo("디자이너");
        assertThat(entity.getBaptismStatus()).isEqualTo(BaptismStatus.PAEDOBAPTISM);
        assertThat(entity.getMbti()).isEqualTo("ENFP");
        assertThat(entity.getIsProvisioned()).isFalse();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
        assertThat(entity.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("toGroupMemberDomain: null 입력 시 null을 반환한다")
    void toGroupMemberDomain_nullInput() {
        assertThat(mapper.toGroupMemberDomain(null)).isNull();
    }

    @Test
    @DisplayName("toGroupMemberDomain: GroupMemberJpaEntity -> GroupMember 변환 시 내부 Member도 함께 변환된다")
    void toGroupMemberDomain() {
        UUID groupMemberId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        MemberJpaEntity memberEntity = MemberJpaEntity.builder()
                .id(memberId)
                .name("박지민")
                .sex(Sex.F)
                .build();

        GroupJpaEntity groupEntity = GroupJpaEntity.builder().id(groupId).build();

        GroupMemberJpaEntity entity = GroupMemberJpaEntity.builder()
                .id(groupMemberId)
                .group(groupEntity)
                .member(memberEntity)
                .role(GroupMemberRole.LEADER)
                .status(GroupMemberStatus.ACTIVE)
                .build();

        GroupMember domain = mapper.toGroupMemberDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(groupMemberId);
        assertThat(domain.getGroupId().getValue()).isEqualTo(groupId);
        assertThat(domain.getMember()).isNotNull();
        assertThat(domain.getMember().getId().getValue()).isEqualTo(memberId);
        assertThat(domain.getMember().getName()).isEqualTo("박지민");
        assertThat(domain.getRole()).isEqualTo(GroupMemberRole.LEADER);
        assertThat(domain.getStatus()).isEqualTo(GroupMemberStatus.ACTIVE);
    }

    @Test
    @DisplayName("toGroupMemberEntity: null 입력 시 null을 반환한다")
    void toGroupMemberEntity_nullInput() {
        assertThat(mapper.toGroupMemberEntity(null)).isNull();
    }

    @Test
    @DisplayName("toGroupMemberEntity: GroupMember -> GroupMemberJpaEntity 변환 시 stub 엔티티가 올바르게 생성된다")
    void toGroupMemberEntity() {
        UUID gmId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        GroupMember domain = GroupMember.builder()
                .id(GroupMemberId.from(gmId))
                .groupId(GroupId.from(groupId))
                .member(Member.builder()
                        .id(MemberId.from(memberId))
                        .name("stub")
                        .build())
                .role(GroupMemberRole.MEMBER)
                .status(GroupMemberStatus.GRADUATED)
                .build();

        GroupMemberJpaEntity entity = mapper.toGroupMemberEntity(domain);

        assertThat(entity.getId()).isEqualTo(gmId);
        assertThat(entity.getGroup().getId()).isEqualTo(groupId);
        assertThat(entity.getMember().getId()).isEqualTo(memberId);
        assertThat(entity.getRole()).isEqualTo(GroupMemberRole.MEMBER);
        assertThat(entity.getStatus()).isEqualTo(GroupMemberStatus.GRADUATED);
    }
}
