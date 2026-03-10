package mitl.IntoTheHeaven.adapter.out.persistence.mapper;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MediaJpaEntity;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.MediaId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MediaPersistenceMapperTest {

    private final MediaPersistenceMapper mapper = new MediaPersistenceMapper();

    @Test
    @DisplayName("toDomain: null 입력 시 null을 반환한다")
    void toDomain_nullInput() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    @DisplayName("toDomain: JPA Entity -> Domain 변환 시 모든 필드와 열거형이 정확하게 매핑된다")
    void toDomain() {
        UUID id = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        MediaJpaEntity entity = MediaJpaEntity.builder()
                .id(id)
                .mediaType(MediaType.THUMBNAIL)
                .entityType(EntityType.GATHERING)
                .entityId(entityId)
                .fileGroupId("file-group-abc")
                .url("https://r2.example.com/image.jpg")
                .createdAt(now)
                .deletedAt(now.plusDays(7))
                .build();

        Media domain = mapper.toDomain(entity);

        assertThat(domain.getId().getValue()).isEqualTo(id);
        assertThat(domain.getMediaType()).isEqualTo(MediaType.THUMBNAIL);
        assertThat(domain.getEntityType()).isEqualTo(EntityType.GATHERING);
        assertThat(domain.getEntityId()).isEqualTo(entityId);
        assertThat(domain.getFileGroupId()).isEqualTo("file-group-abc");
        assertThat(domain.getUrl()).isEqualTo("https://r2.example.com/image.jpg");
        assertThat(domain.getCreatedAt()).isEqualTo(now);
        assertThat(domain.getDeletedAt()).isEqualTo(now.plusDays(7));
    }

    @Test
    @DisplayName("toEntity: null 입력 시 null을 반환한다")
    void toEntity_nullInput() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    @DisplayName("toEntity: Domain -> JPA Entity 변환 시 모든 필드가 정확하게 매핑된다")
    void toEntity() {
        UUID id = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Media domain = Media.builder()
                .id(MediaId.from(id))
                .mediaType(MediaType.MEDIUM)
                .entityType(EntityType.GROUP)
                .entityId(entityId)
                .fileGroupId("fg-xyz")
                .url("https://r2.example.com/medium.png")
                .createdAt(now)
                .deletedAt(now.plusDays(3))
                .build();

        MediaJpaEntity entity = mapper.toEntity(domain);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getMediaType()).isEqualTo(MediaType.MEDIUM);
        assertThat(entity.getEntityType()).isEqualTo(EntityType.GROUP);
        assertThat(entity.getEntityId()).isEqualTo(entityId);
        assertThat(entity.getFileGroupId()).isEqualTo("fg-xyz");
        assertThat(entity.getUrl()).isEqualTo("https://r2.example.com/medium.png");
        assertThat(entity.getCreatedAt()).isEqualTo(now);
        assertThat(entity.getDeletedAt()).isEqualTo(now.plusDays(3));
    }

    @Test
    @DisplayName("toDomainList: null 입력 시 빈 리스트를 반환한다")
    void toDomainList_nullInput() {
        List<Media> result = mapper.toDomainList(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toDomainList: 엔티티 리스트를 도메인 리스트로 올바르게 변환한다")
    void toDomainList() {
        MediaJpaEntity entity1 = MediaJpaEntity.builder()
                .id(UUID.randomUUID())
                .mediaType(MediaType.THUMBNAIL)
                .entityType(EntityType.MEMBER)
                .entityId(UUID.randomUUID())
                .url("https://example.com/1.jpg")
                .build();

        MediaJpaEntity entity2 = MediaJpaEntity.builder()
                .id(UUID.randomUUID())
                .mediaType(MediaType.MEDIUM)
                .entityType(EntityType.CHURCH)
                .entityId(UUID.randomUUID())
                .url("https://example.com/2.jpg")
                .build();

        List<Media> result = mapper.toDomainList(List.of(entity1, entity2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMediaType()).isEqualTo(MediaType.THUMBNAIL);
        assertThat(result.get(1).getMediaType()).isEqualTo(MediaType.MEDIUM);
    }

    @Test
    @DisplayName("toEntityList: null 입력 시 빈 리스트를 반환한다")
    void toEntityList_nullInput() {
        List<MediaJpaEntity> result = mapper.toEntityList(null);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("toEntityList: 도메인 리스트를 엔티티 리스트로 올바르게 변환한다")
    void toEntityList() {
        Media domain1 = Media.builder()
                .id(MediaId.from(UUID.randomUUID()))
                .mediaType(MediaType.THUMBNAIL)
                .entityType(EntityType.VISIT)
                .entityId(UUID.randomUUID())
                .url("https://example.com/a.jpg")
                .build();

        Media domain2 = Media.builder()
                .id(MediaId.from(UUID.randomUUID()))
                .mediaType(MediaType.MEDIUM)
                .entityType(EntityType.GATHERING)
                .entityId(UUID.randomUUID())
                .url("https://example.com/b.jpg")
                .build();

        List<MediaJpaEntity> result = mapper.toEntityList(List.of(domain1, domain2));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEntityType()).isEqualTo(EntityType.VISIT);
        assertThat(result.get(1).getEntityType()).isEqualTo(EntityType.GATHERING);
    }
}
