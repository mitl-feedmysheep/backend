package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GatheringTest {

    private Gathering createGathering(List<Media> medias, List<GatheringMember> members) {
        return Gathering.builder()
                .id(GatheringId.from(UUID.randomUUID()))
                .name("주일 모임")
                .description("주일 오후 소그룹 모임")
                .date(LocalDate.of(2025, 3, 9))
                .startedAt(Instant.parse("2025-03-09T05:00:00Z"))
                .endedAt(Instant.parse("2025-03-09T07:00:00Z"))
                .place("교회 2층")
                .leaderComment("좋은 시간이었습니다")
                .adminComment("출석률 양호")
                .gatheringMembers(members)
                .medias(medias)
                .build();
    }

    private Media createMedia(MediaType mediaType, String url, LocalDateTime createdAt) {
        return Media.builder()
                .id(MediaId.from(UUID.randomUUID()))
                .mediaType(mediaType)
                .entityType(EntityType.GATHERING)
                .entityId(UUID.randomUUID())
                .url(url)
                .createdAt(createdAt)
                .build();
    }

    private GatheringMember createGatheringMember() {
        return GatheringMember.builder()
                .id(GatheringMemberId.from(UUID.randomUUID()))
                .gatheringId(GatheringId.from(UUID.randomUUID()))
                .worshipAttendance(true)
                .gatheringAttendance(true)
                .prayers(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("getThumbnailUrl")
    class GetThumbnailUrl {

        @Test
        @DisplayName("THUMBNAIL 타입 미디어가 있으면 URL을 반환한다")
        void returnsThumbnailUrlWhenPresent() {
            Media thumbnail = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg", LocalDateTime.now());
            Gathering gathering = createGathering(List.of(thumbnail), Collections.emptyList());

            assertThat(gathering.getThumbnailUrl()).isPresent().contains("https://cdn.example.com/thumb.jpg");
        }

        @Test
        @DisplayName("THUMBNAIL 타입이 없으면 빈 Optional을 반환한다")
        void returnsEmptyWhenNoThumbnail() {
            Media medium = createMedia(MediaType.MEDIUM, "https://cdn.example.com/photo.jpg", LocalDateTime.now());
            Gathering gathering = createGathering(List.of(medium), Collections.emptyList());

            assertThat(gathering.getThumbnailUrl()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getAllPhotoUrls")
    class GetAllPhotoUrls {

        @Test
        @DisplayName("MEDIUM 타입 미디어의 URL을 createdAt 오름차순으로 반환한다")
        void returnsPhotoUrlsSortedByCreatedAtAscending() {
            LocalDateTime now = LocalDateTime.of(2025, 3, 9, 12, 0);
            Media photo3 = createMedia(MediaType.MEDIUM, "https://cdn.example.com/third.jpg", now.plusHours(2));
            Media photo1 = createMedia(MediaType.MEDIUM, "https://cdn.example.com/first.jpg", now);
            Media photo2 = createMedia(MediaType.MEDIUM, "https://cdn.example.com/second.jpg", now.plusHours(1));
            Gathering gathering = createGathering(List.of(photo3, photo1, photo2), Collections.emptyList());

            List<String> result = gathering.getAllPhotoUrls();

            assertThat(result).containsExactly(
                    "https://cdn.example.com/first.jpg",
                    "https://cdn.example.com/second.jpg",
                    "https://cdn.example.com/third.jpg"
            );
        }

        @Test
        @DisplayName("THUMBNAIL 타입은 제외하고 MEDIUM 타입만 반환한다")
        void excludesThumbnailMediaType() {
            LocalDateTime now = LocalDateTime.now();
            Media thumbnail = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg", now);
            Media photo = createMedia(MediaType.MEDIUM, "https://cdn.example.com/photo.jpg", now.plusMinutes(1));
            Gathering gathering = createGathering(List.of(thumbnail, photo), Collections.emptyList());

            List<String> result = gathering.getAllPhotoUrls();

            assertThat(result).containsExactly("https://cdn.example.com/photo.jpg");
        }

        @Test
        @DisplayName("MEDIUM 타입이 없으면 빈 리스트를 반환한다")
        void returnsEmptyListWhenNoMediumMedia() {
            Media thumbnail = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg", LocalDateTime.now());
            Gathering gathering = createGathering(List.of(thumbnail), Collections.emptyList());

            assertThat(gathering.getAllPhotoUrls()).isEmpty();
        }

        @Test
        @DisplayName("미디어가 없으면 빈 리스트를 반환한다")
        void returnsEmptyListWhenNoMedias() {
            Gathering gathering = createGathering(new ArrayList<>(), Collections.emptyList());

            assertThat(gathering.getAllPhotoUrls()).isEmpty();
        }
    }

    @Nested
    @DisplayName("getMainImageUrl")
    class GetMainImageUrl {

        @Test
        @DisplayName("createdAt 기준 가장 오래된 MEDIUM 미디어의 URL을 반환한다")
        void returnsOldestMediumUrl() {
            LocalDateTime now = LocalDateTime.of(2025, 3, 9, 12, 0);
            Media newer = createMedia(MediaType.MEDIUM, "https://cdn.example.com/newer.jpg", now.plusHours(1));
            Media older = createMedia(MediaType.MEDIUM, "https://cdn.example.com/older.jpg", now);
            Gathering gathering = createGathering(List.of(newer, older), Collections.emptyList());

            assertThat(gathering.getMainImageUrl()).isPresent().contains("https://cdn.example.com/older.jpg");
        }

        @Test
        @DisplayName("MEDIUM 타입이 없으면 빈 Optional을 반환한다")
        void returnsEmptyWhenNoMedium() {
            Gathering gathering = createGathering(new ArrayList<>(), Collections.emptyList());

            assertThat(gathering.getMainImageUrl()).isEmpty();
        }
    }

    @Nested
    @DisplayName("hasPhotos")
    class HasPhotos {

        @Test
        @DisplayName("MEDIUM 타입 미디어가 있으면 true를 반환한다")
        void returnsTrueWhenMediumExists() {
            Media photo = createMedia(MediaType.MEDIUM, "https://cdn.example.com/photo.jpg", LocalDateTime.now());
            Gathering gathering = createGathering(List.of(photo), Collections.emptyList());

            assertThat(gathering.hasPhotos()).isTrue();
        }

        @Test
        @DisplayName("THUMBNAIL만 있고 MEDIUM이 없으면 false를 반환한다")
        void returnsFalseWhenOnlyThumbnail() {
            Media thumbnail = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg", LocalDateTime.now());
            Gathering gathering = createGathering(List.of(thumbnail), Collections.emptyList());

            assertThat(gathering.hasPhotos()).isFalse();
        }

        @Test
        @DisplayName("미디어가 없으면 false를 반환한다")
        void returnsFalseWhenNoMedias() {
            Gathering gathering = createGathering(new ArrayList<>(), Collections.emptyList());

            assertThat(gathering.hasPhotos()).isFalse();
        }
    }

    @Nested
    @DisplayName("getPhotoCount")
    class GetPhotoCount {

        @Test
        @DisplayName("MEDIUM 타입 미디어의 개수를 반환한다")
        void returnsMediumCount() {
            LocalDateTime now = LocalDateTime.now();
            Media photo1 = createMedia(MediaType.MEDIUM, "https://cdn.example.com/1.jpg", now);
            Media photo2 = createMedia(MediaType.MEDIUM, "https://cdn.example.com/2.jpg", now.plusMinutes(1));
            Media thumbnail = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg", now);
            Gathering gathering = createGathering(List.of(photo1, photo2, thumbnail), Collections.emptyList());

            assertThat(gathering.getPhotoCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("MEDIUM 타입이 없으면 0을 반환한다")
        void returnsZeroWhenNoMedium() {
            Gathering gathering = createGathering(new ArrayList<>(), Collections.emptyList());

            assertThat(gathering.getPhotoCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("hasMedia")
    class HasMedia {

        @Test
        @DisplayName("미디어가 있으면 true를 반환한다")
        void returnsTrueWhenMediasExist() {
            Media media = createMedia(MediaType.THUMBNAIL, "https://cdn.example.com/thumb.jpg", LocalDateTime.now());
            Gathering gathering = createGathering(List.of(media), Collections.emptyList());

            assertThat(gathering.hasMedia()).isTrue();
        }

        @Test
        @DisplayName("미디어가 없으면 false를 반환한다")
        void returnsFalseWhenEmpty() {
            Gathering gathering = createGathering(new ArrayList<>(), Collections.emptyList());

            assertThat(gathering.hasMedia()).isFalse();
        }
    }

    @Nested
    @DisplayName("addGatheringMembers")
    class AddGatheringMembers {

        @Test
        @DisplayName("새로운 멤버를 추가한 새 Gathering을 반환한다")
        void returnsNewGatheringWithAddedMembers() {
            GatheringMember existingMember = createGatheringMember();
            GatheringMember newMember = createGatheringMember();
            Gathering original = createGathering(new ArrayList<>(), new ArrayList<>(List.of(existingMember)));

            Gathering result = original.addGatheringMembers(List.of(newMember));

            assertThat(result.getGatheringMembers()).hasSize(2);
            assertThat(result.getGatheringMembers()).containsExactly(existingMember, newMember);
        }

        @Test
        @DisplayName("원본 Gathering의 멤버 목록은 변경되지 않는다")
        void originalGatheringRemainsUnchanged() {
            GatheringMember existingMember = createGatheringMember();
            GatheringMember newMember = createGatheringMember();
            Gathering original = createGathering(new ArrayList<>(), new ArrayList<>(List.of(existingMember)));

            original.addGatheringMembers(List.of(newMember));

            assertThat(original.getGatheringMembers()).hasSize(1);
            assertThat(original.getGatheringMembers()).containsExactly(existingMember);
        }

        @Test
        @DisplayName("빈 리스트를 추가하면 기존 멤버만 유지된다")
        void addingEmptyListPreservesExistingMembers() {
            GatheringMember existingMember = createGatheringMember();
            Gathering original = createGathering(new ArrayList<>(), new ArrayList<>(List.of(existingMember)));

            Gathering result = original.addGatheringMembers(Collections.emptyList());

            assertThat(result.getGatheringMembers()).hasSize(1);
            assertThat(result.getGatheringMembers()).containsExactly(existingMember);
        }

        @Test
        @DisplayName("다른 필드들은 보존된다")
        void preservesOtherFields() {
            GatheringId id = GatheringId.from(UUID.randomUUID());
            Gathering original = Gathering.builder()
                    .id(id)
                    .name("주일 모임")
                    .description("설명")
                    .date(LocalDate.of(2025, 3, 9))
                    .place("교회")
                    .gatheringMembers(new ArrayList<>())
                    .build();

            Gathering result = original.addGatheringMembers(List.of(createGatheringMember()));

            assertThat(result.getId()).isEqualTo(id);
            assertThat(result.getName()).isEqualTo("주일 모임");
            assertThat(result.getDescription()).isEqualTo("설명");
            assertThat(result.getDate()).isEqualTo(LocalDate.of(2025, 3, 9));
            assertThat(result.getPlace()).isEqualTo("교회");
        }
    }

    @Test
    @DisplayName("Builder.Default로 인해 medias를 지정하지 않으면 빈 리스트가 된다")
    void defaultMediasIsEmptyList() {
        Gathering gathering = Gathering.builder()
                .id(GatheringId.from(UUID.randomUUID()))
                .name("test")
                .gatheringMembers(Collections.emptyList())
                .build();

        assertThat(gathering.getMedias()).isNotNull().isEmpty();
    }
}
