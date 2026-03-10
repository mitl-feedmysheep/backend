package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChurchMemberPersistenceMapperTest {

    private final ChurchMemberPersistenceMapper mapper = new ChurchMemberPersistenceMapper();

    @Test
    @DisplayName("JPA Entity -> Domain 변환 시 중첩된 church/member에서 ID를 올바르게 추출한다")
    void toDomain() {
        UUID id = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();

        ChurchJpaEntity church = ChurchJpaEntity.builder().id(churchId).build();
        MemberJpaEntity member = MemberJpaEntity.builder().id(memberId).build();

        ChurchMemberJpaEntity entity = ChurchMemberJpaEntity.builder()
                .id(id)
                .church(church)
                .member(member)
                .role(ChurchRole.ADMIN)
                .build();

        ChurchMember domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getChurchId().getValue()).isEqualTo(churchId);
        assertThat(domain.getMemberId().getValue()).isEqualTo(memberId);
        assertThat(domain.getRole()).isEqualTo(ChurchRole.ADMIN);
    }

    @Test
    @DisplayName("역할이 MEMBER인 경우 정확히 매핑된다")
    void toDomain_memberRole() {
        ChurchMemberJpaEntity entity = ChurchMemberJpaEntity.builder()
                .id(UUID.randomUUID())
                .church(ChurchJpaEntity.builder().id(UUID.randomUUID()).build())
                .member(MemberJpaEntity.builder().id(UUID.randomUUID()).build())
                .role(ChurchRole.MEMBER)
                .build();

        ChurchMember domain = mapper.toDomain(entity);

        assertThat(domain.getRole()).isEqualTo(ChurchRole.MEMBER);
    }

    @Test
    @DisplayName("역할이 SUPER_ADMIN인 경우 정확히 매핑된다")
    void toDomain_superAdminRole() {
        ChurchMemberJpaEntity entity = ChurchMemberJpaEntity.builder()
                .id(UUID.randomUUID())
                .church(ChurchJpaEntity.builder().id(UUID.randomUUID()).build())
                .member(MemberJpaEntity.builder().id(UUID.randomUUID()).build())
                .role(ChurchRole.SUPER_ADMIN)
                .build();

        ChurchMember domain = mapper.toDomain(entity);

        assertThat(domain.getRole()).isEqualTo(ChurchRole.SUPER_ADMIN);
    }
}
