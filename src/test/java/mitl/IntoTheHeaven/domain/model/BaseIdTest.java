package mitl.IntoTheHeaven.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BaseIdTest {

    @Nested
    @DisplayName("from 팩토리 메서드")
    class FromFactory {

        @Test
        @DisplayName("UUID로부터 MemberId를 생성할 수 있다")
        void createsMemberIdFromUuid() {
            UUID uuid = UUID.randomUUID();

            MemberId memberId = MemberId.from(uuid);

            assertThat(memberId).isNotNull();
            assertThat(memberId.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("getValue()는 생성에 사용된 UUID를 반환한다")
        void getValueReturnsOriginalUuid() {
            UUID uuid = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

            MemberId memberId = MemberId.from(uuid);

            assertThat(memberId.getValue()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        }
    }

    @Nested
    @DisplayName("equals / hashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("같은 UUID로 생성된 ID는 동일하다")
        void sameUuidMeansEqual() {
            UUID uuid = UUID.randomUUID();
            MemberId id1 = MemberId.from(uuid);
            MemberId id2 = MemberId.from(uuid);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("다른 UUID로 생성된 ID는 다르다")
        void differentUuidMeansNotEqual() {
            MemberId id1 = MemberId.from(UUID.randomUUID());
            MemberId id2 = MemberId.from(UUID.randomUUID());

            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("자기 자신과는 동일하다")
        void sameInstanceIsEqual() {
            MemberId id = MemberId.from(UUID.randomUUID());

            assertThat(id).isEqualTo(id);
        }

        @Test
        @DisplayName("null과는 동일하지 않다")
        void notEqualToNull() {
            MemberId id = MemberId.from(UUID.randomUUID());

            assertThat(id).isNotEqualTo(null);
        }

        @Test
        @DisplayName("다른 타입의 ID와는 동일하지 않다")
        void differentIdTypesAreNotEqual() {
            UUID uuid = UUID.randomUUID();
            MemberId memberId = MemberId.from(uuid);
            GroupId groupId = GroupId.from(uuid);

            assertThat(memberId).isNotEqualTo(groupId);
        }
    }

    @Nested
    @DisplayName("다양한 ID 타입 생성 검증")
    class VariousIdTypes {

        @Test
        @DisplayName("GroupId를 UUID로부터 생성할 수 있다")
        void createsGroupId() {
            UUID uuid = UUID.randomUUID();
            GroupId id = GroupId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("GatheringId를 UUID로부터 생성할 수 있다")
        void createsGatheringId() {
            UUID uuid = UUID.randomUUID();
            GatheringId id = GatheringId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("PrayerId를 UUID로부터 생성할 수 있다")
        void createsPrayerId() {
            UUID uuid = UUID.randomUUID();
            PrayerId id = PrayerId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("MediaId를 UUID로부터 생성할 수 있다")
        void createsMediaId() {
            UUID uuid = UUID.randomUUID();
            MediaId id = MediaId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("SermonNoteId를 UUID로부터 생성할 수 있다")
        void createsSermonNoteId() {
            UUID uuid = UUID.randomUUID();
            SermonNoteId id = SermonNoteId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("VisitId를 UUID로부터 생성할 수 있다")
        void createsVisitId() {
            UUID uuid = UUID.randomUUID();
            VisitId id = VisitId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("MessageId를 UUID로부터 생성할 수 있다")
        void createsMessageId() {
            UUID uuid = UUID.randomUUID();
            MessageId id = MessageId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("NotificationId를 UUID로부터 생성할 수 있다")
        void createsNotificationId() {
            UUID uuid = UUID.randomUUID();
            NotificationId id = NotificationId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("VisitMemberId를 UUID로부터 생성할 수 있다")
        void createsVisitMemberId() {
            UUID uuid = UUID.randomUUID();
            VisitMemberId id = VisitMemberId.from(uuid);

            assertThat(id.getValue()).isEqualTo(uuid);
        }
    }
}
