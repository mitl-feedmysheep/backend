package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.domain.model.Media;
import mitl.IntoTheHeaven.domain.model.MediaId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaPort {

    /**
     * 미디어 저장
     */
    Media save(Media media);

    /**
     * 미디어 목록 저장
     */
    List<Media> saveAll(List<Media> medias);

    /**
     * ID로 미디어 조회
     */
    Optional<Media> findById(MediaId mediaId);

    /**
     * 파일 그룹 ID로 미디어 조회 (같은 원본에서 생성된 모든 미디어)
     */
    List<Media> findByFileGroupId(String fileGroupId);

    /**
     * 특정 엔티티의 모든 미디어 조회
     */
    List<Media> findByEntity(EntityType entityType, UUID entityId);

    /**
     * 특정 엔티티의 특정 타입 미디어 조회
     */
    Optional<Media> findByEntityAndType(EntityType entityType, UUID entityId, MediaType mediaType);

    /**
     * 특정 엔티티의 썸네일 조회
     */
    Optional<Media> findThumbnailByEntity(EntityType entityType, UUID entityId);

    /**
     * 특정 엔티티의 메인 이미지 조회 (MEDIUM)
     */
    Optional<Media> findMainImageByEntity(EntityType entityType, UUID entityId);

    /**
     * 특정 엔티티 ID의 모든 미디어 조회 (문자열 ID 기반)
     */
    List<Media> findByEntityId(UUID entityId);

    /**
     * 모든 미디어 조회
     */
    List<Media> findAll();
}
