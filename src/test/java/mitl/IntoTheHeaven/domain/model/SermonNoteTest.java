package mitl.IntoTheHeaven.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SermonNoteTest {

    private final SermonNoteId NOTE_ID = SermonNoteId.from(UUID.randomUUID());
    private final MemberId MEMBER_ID = MemberId.from(UUID.randomUUID());
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 3, 1, 10, 0);

    private SermonNote createSermonNote() {
        return SermonNote.builder()
                .id(NOTE_ID)
                .memberId(MEMBER_ID)
                .title("주일 설교")
                .sermonDate(LocalDate.of(2025, 3, 9))
                .preacher("김목사")
                .serviceType("주일 예배")
                .scripture("요한복음 3:16")
                .content("하나님이 세상을 이처럼 사랑하사...")
                .createdAt(CREATED_AT)
                .updatedAt(CREATED_AT)
                .build();
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("삭제 시 deletedAt이 설정된다")
        void setsDeletedAt() {
            SermonNote note = createSermonNote();

            SermonNote deleted = note.delete();

            assertThat(deleted.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("삭제 시 다른 필드들은 보존된다")
        void preservesOtherFields() {
            SermonNote note = createSermonNote();

            SermonNote deleted = note.delete();

            assertThat(deleted.getId()).isEqualTo(NOTE_ID);
            assertThat(deleted.getMemberId()).isEqualTo(MEMBER_ID);
            assertThat(deleted.getTitle()).isEqualTo("주일 설교");
            assertThat(deleted.getSermonDate()).isEqualTo(LocalDate.of(2025, 3, 9));
            assertThat(deleted.getPreacher()).isEqualTo("김목사");
            assertThat(deleted.getServiceType()).isEqualTo("주일 예배");
            assertThat(deleted.getScripture()).isEqualTo("요한복음 3:16");
            assertThat(deleted.getContent()).isEqualTo("하나님이 세상을 이처럼 사랑하사...");
            assertThat(deleted.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            SermonNote note = createSermonNote();

            note.delete();

            assertThat(note.getDeletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("지정된 필드들이 업데이트된다")
        void updatesSpecifiedFields() {
            SermonNote note = createSermonNote();

            SermonNote updated = note.update(
                    "수정된 제목",
                    LocalDate.of(2025, 3, 16),
                    "박목사",
                    "수요 예배",
                    "로마서 8:28",
                    "수정된 내용"
            );

            assertThat(updated.getTitle()).isEqualTo("수정된 제목");
            assertThat(updated.getSermonDate()).isEqualTo(LocalDate.of(2025, 3, 16));
            assertThat(updated.getPreacher()).isEqualTo("박목사");
            assertThat(updated.getServiceType()).isEqualTo("수요 예배");
            assertThat(updated.getScripture()).isEqualTo("로마서 8:28");
            assertThat(updated.getContent()).isEqualTo("수정된 내용");
        }

        @Test
        @DisplayName("id, memberId, createdAt은 보존된다")
        void preservesImmutableFields() {
            SermonNote note = createSermonNote();

            SermonNote updated = note.update(
                    "수정된 제목",
                    LocalDate.of(2025, 3, 16),
                    "박목사",
                    "수요 예배",
                    "로마서 8:28",
                    "수정된 내용"
            );

            assertThat(updated.getId()).isEqualTo(NOTE_ID);
            assertThat(updated.getMemberId()).isEqualTo(MEMBER_ID);
            assertThat(updated.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            SermonNote note = createSermonNote();

            note.update("수정된 제목", LocalDate.of(2025, 3, 16), "박목사", "수요 예배", "로마서 8:28", "수정된 내용");

            assertThat(note.getTitle()).isEqualTo("주일 설교");
            assertThat(note.getPreacher()).isEqualTo("김목사");
            assertThat(note.getContent()).isEqualTo("하나님이 세상을 이처럼 사랑하사...");
        }
    }
}
