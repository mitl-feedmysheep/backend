package mitl.IntoTheHeaven.domain.model;

import mitl.IntoTheHeaven.domain.enums.EntityType;
import mitl.IntoTheHeaven.domain.enums.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VisitTest {

    private final VisitId VISIT_ID = VisitId.from(UUID.randomUUID());
    private final ChurchId CHURCH_ID = ChurchId.from(UUID.randomUUID());
    private final ChurchMemberId PASTOR_ID = ChurchMemberId.from(UUID.randomUUID());
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 3, 9, 10, 0);

    private Visit createVisit(List<Media> medias) {
        return Visit.builder()
                .id(VISIT_ID)
                .churchId(CHURCH_ID)
                .pastorMemberId(PASTOR_ID)
                .date(LocalDate.of(2025, 3, 9))
                .startedAt(LocalDateTime.of(2025, 3, 9, 14, 0))
                .endedAt(LocalDateTime.of(2025, 3, 9, 16, 0))
                .place("성도 자택")
                .expense(50000)
                .notes("좋은 만남이었습니다")
                .createdAt(CREATED_AT)
                .medias(medias)
                .build();
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("삭제 시 deletedAt이 설정된다")
        void setsDeletedAt() {
            Visit visit = createVisit(new ArrayList<>());

            Visit deleted = visit.delete();

            assertThat(deleted.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("삭제 시 다른 필드들은 보존된다")
        void preservesOtherFields() {
            Visit visit = createVisit(new ArrayList<>());

            Visit deleted = visit.delete();

            assertThat(deleted.getId()).isEqualTo(VISIT_ID);
            assertThat(deleted.getChurchId()).isEqualTo(CHURCH_ID);
            assertThat(deleted.getPastorMemberId()).isEqualTo(PASTOR_ID);
            assertThat(deleted.getDate()).isEqualTo(LocalDate.of(2025, 3, 9));
            assertThat(deleted.getPlace()).isEqualTo("성도 자택");
            assertThat(deleted.getExpense()).isEqualTo(50000);
            assertThat(deleted.getNotes()).isEqualTo("좋은 만남이었습니다");
            assertThat(deleted.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            Visit visit = createVisit(new ArrayList<>());

            visit.delete();

            assertThat(visit.getDeletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("hasPhotos")
    class HasPhotos {

        @Test
        @DisplayName("미디어가 있으면 true를 반환한다")
        void returnsTrueWhenMediasExist() {
            Media photo = Media.builder()
                    .id(MediaId.from(UUID.randomUUID()))
                    .mediaType(MediaType.MEDIUM)
                    .entityType(EntityType.VISIT)
                    .entityId(UUID.randomUUID())
                    .url("https://cdn.example.com/photo.jpg")
                    .createdAt(LocalDateTime.now())
                    .build();
            Visit visit = createVisit(List.of(photo));

            assertThat(visit.hasPhotos()).isTrue();
        }

        @Test
        @DisplayName("미디어가 없으면 false를 반환한다")
        void returnsFalseWhenNoMedias() {
            Visit visit = createVisit(new ArrayList<>());

            assertThat(visit.hasPhotos()).isFalse();
        }
    }

    @Test
    @DisplayName("Builder.Default로 medias를 지정하지 않으면 빈 리스트가 된다")
    void defaultMediasIsEmptyList() {
        Visit visit = Visit.builder()
                .id(VISIT_ID)
                .churchId(CHURCH_ID)
                .date(LocalDate.of(2025, 3, 9))
                .build();

        assertThat(visit.getMedias()).isNotNull().isEmpty();
        assertThat(visit.getVisitMembers()).isNotNull().isEmpty();
    }
}
