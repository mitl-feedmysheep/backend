package mitl.IntoTheHeaven.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.global.common.BaseEntity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "media")
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SQLRestriction("deleted_at is null")
public class MediaJpaEntity extends BaseEntity {

    /**
     * 미디어 타입 (ORIGINAL, THUMBNAIL, RESIZED_SMALL 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false, length = 20)
    private MediaType mediaType;

    /**
     * 연관 엔티티 타입 (GROUP, GATHERING, MEMBER, CHURCH)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 20)
    private EntityType entityType;

    /**
     * 연관 엔티티 ID
     */
    @Column(name = "entity_id", nullable = false, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID entityId;

    /**
     * 저장소 경로 (로컬/임시)
     */
    @Column(name = "storage_path", length = 1024)
    private String storagePath;

    /**
     * CDN URL (마이그레이션 후)
     */
    @Column(name = "url", length = 2048)
    private String url;
}
