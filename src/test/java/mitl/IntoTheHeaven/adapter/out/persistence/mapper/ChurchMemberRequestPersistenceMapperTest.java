package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchMemberRequestJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.MemberJpaEntity;
import mitl.IntoTheHeaven.domain.enums.RequestStatus;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChurchMemberRequestPersistenceMapperTest {

    private final ChurchMemberRequestPersistenceMapper mapper = new ChurchMemberRequestPersistenceMapper();

    @Test
    @DisplayName("null 입력 시 null을 반환한다")
    void toDomain_nullInput() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("JPA Entity -> Domain 변환 시 모든 필드가 정확하게 매핑된다")
    void toDomain() {
        UUID id = UUID.randomUUID();
        UUID memberId = UUID.randomUUID();
        UUID churchId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        MemberJpaEntity member = MemberJpaEntity.builder().id(memberId).build();
        ChurchJpaEntity church = ChurchJpaEntity.builder()
                .id(churchId)
                .name("은혜교회")
                .build();

        ChurchMemberRequestJpaEntity entity = ChurchMemberRequestJpaEntity.builder()
                .id(id)
                .member(member)
                .church(church)
                .status(RequestStatus.PENDING)
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .deletedAt(now.plusDays(1))
                .build();

        ChurchMemberRequest domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getMemberId().getValue()).isEqualTo(memberId);
        assertThat(domain.getChurchId().getValue()).isEqualTo(churchId);
        assertThat(domain.getStatus()).isEqualTo(RequestStatus.PENDING);
        assertThat(domain.getChurchName()).isEqualTo("은혜교회");
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getUpdatedAt()).isEqualTo(now.plusHours(1));
        assertThat(domain.getDeletedAt()).isEqualTo(now.plusDays(1));
    }

    @Test
    @DisplayName("교회 이름이 church.getName()에서 올바르게 추출된다")
    void toDomain_churchNameExtraction() {
        ChurchJpaEntity church = ChurchJpaEntity.builder()
                .id(UUID.randomUUID())
                .name("사랑의교회")
                .build();

        ChurchMemberRequestJpaEntity entity = ChurchMemberRequestJpaEntity.builder()
                .id(UUID.randomUUID())
                .member(MemberJpaEntity.builder().id(UUID.randomUUID()).build())
                .church(church)
                .status(RequestStatus.ACCEPTED)
                .build();

        ChurchMemberRequest domain = mapper.toDomain(entity);

        assertThat(domain.getChurchName()).isEqualTo("사랑의교회");
    }

    @Test
    @DisplayName("상태가 DECLINED인 경우 정확히 매핑된다")
    void toDomain_declinedStatus() {
        ChurchMemberRequestJpaEntity entity = ChurchMemberRequestJpaEntity.builder()
                .id(UUID.randomUUID())
                .member(MemberJpaEntity.builder().id(UUID.randomUUID()).build())
                .church(ChurchJpaEntity.builder().id(UUID.randomUUID()).name("테스트").build())
                .status(RequestStatus.DECLINED)
                .build();

        ChurchMemberRequest domain = mapper.toDomain(entity);

        assertThat(domain.getStatus()).isEqualTo(RequestStatus.DECLINED);
    }
}
