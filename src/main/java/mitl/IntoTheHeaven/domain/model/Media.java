package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@SuperBuilder(toBuilder = true)
public class Media extends DomainEntity<Media, MediaId> {

    private final MediaType mediaType;
    private final EntityType entityType;
    private final UUID entityId;
    private final String storagePath;
    private final String url;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    /**
     * 실제 접근 URL 반환 (우선순위: url > storagePath)
     * 내부적으로 파일에 접근할 때 사용
     */
    public String getAccessUrl() {
        return url != null ? url : storagePath;
    }

    /**
     * 프론트엔드에서 접근 가능한 Public URL 반환
     * R2 URL이 있으면 직접 사용, 없으면 API 엔드포인트 URL
     */
    public String getPublicUrl() {
        if (url != null) {
            // R2 public URL 직접 사용
            return url;
        } else {
            // Fallback to API endpoint (현재는 사용 안 함)
            return "/media/" + getId().getValue();
        }
    }

    /**
     * 미디어 삭제 (soft delete)
     */
    public Media delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
