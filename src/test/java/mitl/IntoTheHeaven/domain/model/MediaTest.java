package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MediaTest {

    private final MediaId MEDIA_ID = MediaId.from(UUID.randomUUID());
    private final UUID ENTITY_ID = UUID.randomUUID();
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 3, 9, 10, 0);

    private Media createMedia() {
        return Media.builder()
                .id(MEDIA_ID)
                .mediaType(MediaType.MEDIUM)
                .entityType(EntityType.GATHERING)
                .entityId(ENTITY_ID)
                .fileGroupId("group-123")
                .url("https://cdn.example.com/photo.jpg")
                .createdAt(CREATED_AT)
                .build();
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("삭제 시 deletedAt이 설정된다")
        void setsDeletedAt() {
            Media media = createMedia();

            Media deleted = media.delete();

            assertThat(deleted.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("삭제 시 다른 필드들은 보존된다")
        void preservesOtherFields() {
            Media media = createMedia();

            Media deleted = media.delete();

            assertThat(deleted.getId()).isEqualTo(MEDIA_ID);
            assertThat(deleted.getMediaType()).isEqualTo(MediaType.MEDIUM);
            assertThat(deleted.getEntityType()).isEqualTo(EntityType.GATHERING);
            assertThat(deleted.getEntityId()).isEqualTo(ENTITY_ID);
            assertThat(deleted.getFileGroupId()).isEqualTo("group-123");
            assertThat(deleted.getUrl()).isEqualTo("https://cdn.example.com/photo.jpg");
            assertThat(deleted.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            Media media = createMedia();

            media.delete();

            assertThat(media.getDeletedAt()).isNull();
        }
    }
}
