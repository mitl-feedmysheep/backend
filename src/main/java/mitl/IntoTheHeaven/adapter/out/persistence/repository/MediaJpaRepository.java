package mitl.IntoTheHeaven.adapter.out.persistence.repository;

import mitl.IntoTheHeaven.adapter.out.persistence.entity.MediaJpaEntity;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaJpaRepository extends JpaRepository<MediaJpaEntity, UUID> {

    /**
     * 특정 엔티티의 모든 미디어 조회
     */
    List<MediaJpaEntity> findByEntityTypeAndEntityId(EntityType entityType, UUID entityId);

    /**
     * 파일 그룹 ID로 미디어 조회 (같은 원본에서 생성된 모든 미디어)
     */
    List<MediaJpaEntity> findByFileGroupId(String fileGroupId);

    /**
     * 특정 엔티티의 특정 타입 미디어 조회
     */
    Optional<MediaJpaEntity> findByEntityTypeAndEntityIdAndMediaType(
            EntityType entityType, UUID entityId, MediaType mediaType);

    /**
     * 특정 엔티티의 썸네일 조회
     */
    default Optional<MediaJpaEntity> findThumbnailByEntity(EntityType entityType, UUID entityId) {
        return findByEntityTypeAndEntityIdAndMediaType(entityType, entityId, MediaType.THUMBNAIL);
    }

    /**
     * 특정 엔티티의 메인 이미지 조회 (MEDIUM)
     */
    default Optional<MediaJpaEntity> findMainImageByEntity(EntityType entityType, UUID entityId) {
        return findByEntityTypeAndEntityIdAndMediaType(entityType, entityId, MediaType.MEDIUM);
    }

}
