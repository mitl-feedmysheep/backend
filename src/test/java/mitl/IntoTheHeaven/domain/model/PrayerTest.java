package mitl.IntoTheHeaven.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PrayerTest {

    private final PrayerId PRAYER_ID = PrayerId.from(UUID.randomUUID());
    private final MemberId MEMBER_ID = MemberId.from(UUID.randomUUID());
    private final GatheringMemberId GATHERING_MEMBER_ID = GatheringMemberId.from(UUID.randomUUID());
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 3, 9, 14, 0);

    private Prayer createPrayer() {
        return Prayer.builder()
                .id(PRAYER_ID)
                .memberId(MEMBER_ID)
                .gatheringMemberId(GATHERING_MEMBER_ID)
                .prayerRequest("건강을 위해 기도합니다")
                .description("허리 통증이 나아지기를")
                .isAnswered(false)
                .groupName("청년부 1셀")
                .gatheringDate(LocalDate.of(2025, 3, 9))
                .createdAt(CREATED_AT)
                .build();
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("삭제 시 deletedAt이 설정된다")
        void setsDeletedAt() {
            Prayer prayer = createPrayer();

            Prayer deleted = prayer.delete();

            assertThat(deleted.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("삭제 시 다른 필드들은 보존된다")
        void preservesOtherFields() {
            Prayer prayer = createPrayer();

            Prayer deleted = prayer.delete();

            assertThat(deleted.getId()).isEqualTo(PRAYER_ID);
            assertThat(deleted.getMemberId()).isEqualTo(MEMBER_ID);
            assertThat(deleted.getGatheringMemberId()).isEqualTo(GATHERING_MEMBER_ID);
            assertThat(deleted.getPrayerRequest()).isEqualTo("건강을 위해 기도합니다");
            assertThat(deleted.getDescription()).isEqualTo("허리 통증이 나아지기를");
            assertThat(deleted.isAnswered()).isFalse();
            assertThat(deleted.getGroupName()).isEqualTo("청년부 1셀");
            assertThat(deleted.getGatheringDate()).isEqualTo(LocalDate.of(2025, 3, 9));
            assertThat(deleted.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            Prayer prayer = createPrayer();

            prayer.delete();

            assertThat(prayer.getDeletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("markAnswered")
    class MarkAnswered {

        @Test
        @DisplayName("true로 설정하면 응답됨 상태가 된다")
        void setsIsAnsweredToTrue() {
            Prayer prayer = createPrayer();
            assertThat(prayer.isAnswered()).isFalse();

            Prayer answered = prayer.markAnswered(true);

            assertThat(answered.isAnswered()).isTrue();
        }

        @Test
        @DisplayName("false로 설정하면 미응답 상태가 된다")
        void setsIsAnsweredToFalse() {
            Prayer prayer = Prayer.builder()
                    .id(PRAYER_ID)
                    .memberId(MEMBER_ID)
                    .prayerRequest("기도제목")
                    .isAnswered(true)
                    .createdAt(CREATED_AT)
                    .build();

            Prayer unanswered = prayer.markAnswered(false);

            assertThat(unanswered.isAnswered()).isFalse();
        }

        @Test
        @DisplayName("다른 필드들은 보존된다")
        void preservesOtherFields() {
            Prayer prayer = createPrayer();

            Prayer answered = prayer.markAnswered(true);

            assertThat(answered.getId()).isEqualTo(PRAYER_ID);
            assertThat(answered.getMemberId()).isEqualTo(MEMBER_ID);
            assertThat(answered.getPrayerRequest()).isEqualTo("건강을 위해 기도합니다");
            assertThat(answered.getDescription()).isEqualTo("허리 통증이 나아지기를");
            assertThat(answered.getCreatedAt()).isEqualTo(CREATED_AT);
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            Prayer prayer = createPrayer();

            prayer.markAnswered(true);

            assertThat(prayer.isAnswered()).isFalse();
        }
    }
}
