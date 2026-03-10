package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.GroupType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GroupTest {

    private Group createGroup(List<Media> medias) {
        return Group.builder()
                .id(GroupId.from(UUID.randomUUID()))
                .name("청년부 1셀")
                .description("청년부 첫번째 소그룹")
                .churchId(ChurchId.from(UUID.randomUUID()))
                .type(GroupType.NORMAL)
                .startDate(LocalDate.of(2025, 1, 1))
                .endDate(LocalDate.of(2025, 12, 31))
                .medias(medias)
                .build();
    }

    private Media createMedia(MediaType mediaType, String url) {
        return Media.builder()
                .id(MediaId.from(UUID.randomUUID()))
                .mediaType(mediaType)
                .entityType(EntityType.GROUP)
                .entityId(UUID.randomUUID())
                .url(url)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getThumbnailUrl")
    class GetThumbnailUrl {

        @Test
        @DisplayName("THUMBNAIL 타입 미디어가 있으면 URL을 반환한다")
        void returnsThumbnailUrlWhenPresent() {
            Media thumbnail = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg");
            Group group = createGroup(List.of(thumbnail));

            Optional<String> result = group.getThumbnailUrl();

            assertThat(result).isPresent().contains("https://cdn.example.com/thumb.jpg");
        }

        @Test
        @DisplayName("THUMBNAIL 타입 미디어가 없으면 빈 Optional을 반환한다")
        void returnsEmptyWhenNoThumbnail() {
            Media medium = createMedia(MediaType.MEDIUM, "https://cdn.example.com/main.jpg");
            Group group = createGroup(List.of(medium));

            Optional<String> result = group.getThumbnailUrl();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("미디어가 비어있으면 빈 Optional을 반환한다")
        void returnsEmptyWhenNoMedias() {
            Group group = createGroup(new ArrayList<>());

            Optional<String> result = group.getThumbnailUrl();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getMainImageUrl")
    class GetMainImageUrl {

        @Test
        @DisplayName("MEDIUM 타입 미디어가 있으면 URL을 반환한다")
        void returnsMainImageUrlWhenPresent() {
            Media medium = createMedia(MediaType.MEDIUM, "https://cdn.example.com/main.jpg");
            Group group = createGroup(List.of(medium));

            Optional<String> result = group.getMainImageUrl();

            assertThat(result).isPresent().contains("https://cdn.example.com/main.jpg");
        }

        @Test
        @DisplayName("MEDIUM 타입 미디어가 없으면 빈 Optional을 반환한다")
        void returnsEmptyWhenNoMedium() {
            Media thumbnail = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg");
            Group group = createGroup(List.of(thumbnail));

            Optional<String> result = group.getMainImageUrl();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 MEDIUM 미디어 중 첫 번째 URL을 반환한다")
        void returnsFirstMediumUrl() {
            Media medium1 = createMedia(MediaType.MEDIUM, "https://cdn.example.com/first.jpg");
            Media medium2 = createMedia(MediaType.MEDIUM, "https://cdn.example.com/second.jpg");
            Group group = createGroup(List.of(medium1, medium2));

            Optional<String> result = group.getMainImageUrl();

            assertThat(result).isPresent().contains("https://cdn.example.com/first.jpg");
        }
    }

    @Nested
    @DisplayName("hasMedia")
    class HasMedia {

        @Test
        @DisplayName("미디어가 있으면 true를 반환한다")
        void returnsTrueWhenMediasExist() {
            Media media = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg");
            Group group = createGroup(List.of(media));

            assertThat(group.hasMedia()).isTrue();
        }

        @Test
        @DisplayName("미디어가 없으면 false를 반환한다")
        void returnsFalseWhenNoMedias() {
            Group group = createGroup(new ArrayList<>());

            assertThat(group.hasMedia()).isFalse();
        }
    }

    @Nested
    @DisplayName("hasThumbnail")
    class HasThumbnail {

        @Test
        @DisplayName("THUMBNAIL 미디어가 있으면 true를 반환한다")
        void returnsTrueWhenThumbnailExists() {
            Media thumbnail = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg");
            Group group = createGroup(List.of(thumbnail));

            assertThat(group.hasThumbnail()).isTrue();
        }

        @Test
        @DisplayName("THUMBNAIL 미디어가 없으면 false를 반환한다")
        void returnsFalseWhenNoThumbnail() {
            Media medium = createMedia(MediaType.MEDIUM, "https://cdn.example.com/main.jpg");
            Group group = createGroup(List.of(medium));

            assertThat(group.hasThumbnail()).isFalse();
        }

        @Test
        @DisplayName("미디어가 비어있으면 false를 반환한다")
        void returnsFalseWhenNoMedias() {
            Group group = createGroup(new ArrayList<>());

            assertThat(group.hasThumbnail()).isFalse();
        }
    }

    @Test
    @DisplayName("Builder.Default로 인해 medias를 지정하지 않으면 빈 리스트가 된다")
    void defaultMediasIsEmptyList() {
        Group group = Group.builder()
                .id(GroupId.from(UUID.randomUUID()))
                .name("test")
                .churchId(ChurchId.from(UUID.randomUUID()))
                .type(GroupType.NORMAL)
                .build();

        assertThat(group.getMedias()).isNotNull().isEmpty();
        assertThat(group.hasMedia()).isFalse();
    }
}
