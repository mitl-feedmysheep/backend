package mitl.IntoTheHeaven.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VisitMemberTest {

    private final VisitMemberId VISIT_MEMBER_ID = VisitMemberId.from(UUID.randomUUID());
    private final VisitId VISIT_ID = VisitId.from(UUID.randomUUID());
    private final ChurchMemberId CHURCH_MEMBER_ID = ChurchMemberId.from(UUID.randomUUID());
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2025, 3, 9, 14, 0);

    private VisitMember createVisitMember() {
        return VisitMember.builder()
                .id(VISIT_MEMBER_ID)
                .visitId(VISIT_ID)
                .churchMemberId(CHURCH_MEMBER_ID)
                .story("심방 이야기")
                .createdAt(CREATED_AT)
                .prayers(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("삭제 시 deletedAt이 설정된다")
        void setsDeletedAt() {
            VisitMember visitMember = createVisitMember();

            VisitMember deleted = visitMember.delete();

            assertThat(deleted.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("삭제 시 다른 필드들은 보존된다")
        void preservesOtherFields() {
            VisitMember visitMember = createVisitMember();

            VisitMember deleted = visitMember.delete();

            assertThat(deleted.getId()).isEqualTo(VISIT_MEMBER_ID);
            assertThat(deleted.getVisitId()).isEqualTo(VISIT_ID);
            assertThat(deleted.getChurchMemberId()).isEqualTo(CHURCH_MEMBER_ID);
            assertThat(deleted.getStory()).isEqualTo("심방 이야기");
            assertThat(deleted.getCreatedAt()).isEqualTo(CREATED_AT);
            assertThat(deleted.getPrayers()).isEmpty();
        }

        @Test
        @DisplayName("원본 객체는 변경되지 않는다")
        void originalRemainsUnchanged() {
            VisitMember visitMember = createVisitMember();

            visitMember.delete();

            assertThat(visitMember.getDeletedAt()).isNull();
        }
    }

    @Test
    @DisplayName("Builder.Default로 prayers를 지정하지 않으면 빈 리스트가 된다")
    void defaultPrayersIsEmptyList() {
        VisitMember visitMember = VisitMember.builder()
                .id(VISIT_MEMBER_ID)
                .visitId(VISIT_ID)
                .churchMemberId(CHURCH_MEMBER_ID)
                .build();

        assertThat(visitMember.getPrayers()).isNotNull().isEmpty();
    }
}
