package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.ChurchJpaEntity;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ChurchPersistenceMapperTest {

    private final ChurchPersistenceMapper mapper = new ChurchPersistenceMapper();

    @Test
    @DisplayName("JPA Entity -> Domain 변환 시 모든 필드가 정확하게 매핑된다")
    void toDomain() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        ChurchJpaEntity entity = ChurchJpaEntity.builder()
                .id(id)
                .name("열린교회")
                .location("서울시 강남구")
                .number("02-1234-5678")
                .homepageUrl("https://church.example.com")
                .description("열린교회입니다")
                .createdAt(now)
                .updatedAt(now.plusHours(1))
                .deletedAt(now.plusDays(1))
                .build();

        Church domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("열린교회");
        assertThat(domain.getLocation()).isEqualTo("서울시 강남구");
        assertThat(domain.getNumber()).isEqualTo("02-1234-5678");
        assertThat(domain.getHomepageUrl()).isEqualTo("https://church.example.com");
        assertThat(domain.getDescription()).isEqualTo("열린교회입니다");
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getUpdatedAt()).isEqualTo(now.plusHours(1));
        assertThat(domain.getDeletedAt()).isEqualTo(now.plusDays(1));
    }

    @Test
    @DisplayName("Domain -> JPA Entity 변환 시 createdAt/updatedAt/deletedAt은 포함하지 않는다")
    void toEntity() {
        UUID id = UUID.randomUUID();

        Church domain = Church.builder()
                .id(ChurchId.from(id))
                .name("새빛교회")
                .location("부산시 해운대구")
                .number("051-987-6543")
                .homepageUrl("https://newlight.church")
                .description("새빛교회 설명")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deletedAt(LocalDateTime.now())
                .build();

        ChurchJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo("새빛교회");
        assertThat(entity.getLocation()).isEqualTo("부산시 해운대구");
        assertThat(entity.getNumber()).isEqualTo("051-987-6543");
        assertThat(entity.getHomepageUrl()).isEqualTo("https://newlight.church");
        assertThat(entity.getDescription()).isEqualTo("새빛교회 설명");
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
        assertThat(entity.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("Entity -> Domain -> Entity 라운드트립 시 핵심 필드가 보존된다")
    void roundtrip() {
        UUID id = UUID.randomUUID();

        ChurchJpaEntity original = ChurchJpaEntity.builder()
                .id(id)
                .name("소망교회")
                .location("대전시 유성구")
                .number("042-111-2222")
                .homepageUrl(null)
                .description(null)
                .build();

        Church domain = mapper.toDomain(original);
        ChurchJpaEntity result = mapper.toEntity(domain);

        assertThat(result.getId()).isEqualTo(original.getId());
        assertThat(result.getName()).isEqualTo(original.getName());
        assertThat(result.getLocation()).isEqualTo(original.getLocation());
        assertThat(result.getNumber()).isEqualTo(original.getNumber());
        assertThat(result.getHomepageUrl()).isNull();
        assertThat(result.getDescription()).isNull();
    }
}
