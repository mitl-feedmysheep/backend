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
    private final String fileGroupId;
    private final String url;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    /**
     * 미디어 삭제 (soft delete)
     */
    public Media delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }
}
