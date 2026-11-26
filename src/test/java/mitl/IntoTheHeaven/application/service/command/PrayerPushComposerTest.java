package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PrayerPushCandidate;
import mitl.IntoTheHeaven.application.service.command.PrayerPushComposer.Group;
import mitl.IntoTheHeaven.application.service.command.PushDispatchService.PushMessage;
import mitl.IntoTheHeaven.domain.enums.PushType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PrayerPushComposer")
class PrayerPushComposerTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 26);
    private static final UUID MEMBER = UUID.randomUUID();

    private static PrayerPushCandidate personal(String text, LocalDateTime createdAt) {
        return new PrayerPushCandidate(UUID.randomUUID(), MEMBER, text, true, null, createdAt);
    }

    private static PrayerPushCandidate gathering(String text, LocalDate date) {
        return new PrayerPushCandidate(UUID.randomUUID(), MEMBER, text, false, date, date.atStartOfDay());
    }

    private static PushDeliveryLog log(String templateKey, String entityId, LocalDate day) {
        return PushDeliveryLog.builder()
                .id(UUID.randomUUID())
                .memberId(MemberId.from(MEMBER))
                .pushType(PushType.DAILY_PRAYER)
                .templateKey(templateKey)
                .entityType(entityId == null ? null : "PRAYER")
                .entityId(entityId)
                .sendResult("SUCCESS")
                .sentAt(day.atTime(0, 0))
                .build();
    }

    /** 여러 날 연속으로 보낸다고 가정하고, 매일의 결과를 이력에 쌓아 다음 날 입력으로 쓴다 */
    private static List<PushMessage> simulate(int days, List<PrayerPushCandidate> candidates) {
        List<PushDeliveryLog> history = new ArrayList<>();
        List<PushMessage> sent = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate day = TODAY.plusDays(i);
            PushMessage m = PrayerPushComposer.compose(day, candidates, history);
            sent.add(m);
            history.add(log(m.templateKey(), m.entityId(), day));
        }
        return sent;
    }

    @Nested
    @DisplayName("A: 보여줄 기도제목이 있을 때")
    class GroupA {

        @Test
        @DisplayName("이번 주 모임 기도제목을 본문으로, 해당 기도제목으로 이동하는 링크를 만든다")
        void thisWeekGatheringPrayer() {
            PrayerPushCandidate p = gathering("이직 준비 중인데 지혜로운 선택을", TODAY.minusDays(3));

            PushMessage m = PrayerPushComposer.compose(TODAY, List.of(p), List.of());

            assertThat(m.type()).isEqualTo(PushType.DAILY_PRAYER);
            assertThat(PrayerPushComposer.groupOf(m.templateKey())).isEqualTo(Group.A);
            assertThat(m.body()).isEqualTo("이직 준비 중인데 지혜로운 선택을");
            assertThat(m.url()).isEqualTo("/prayers?highlight=" + p.prayerId());
            assertThat(m.entityType()).isEqualTo("PRAYER");
            assertThat(m.entityId()).isEqualTo(p.prayerId().toString());
        }

        @Test
        @DisplayName("나의 기도는 작성일과 무관하게 후보가 된다")
        void personalPrayerIsAlwaysCandidate() {
            PrayerPushCandidate p = personal("부모님 건강", TODAY.minusMonths(5).atStartOfDay());

            PushMessage m = PrayerPushComposer.compose(TODAY, List.of(p), List.of());

            assertThat(PrayerPushComposer.groupOf(m.templateKey())).isEqualTo(Group.A);
            assertThat(m.entityId()).isEqualTo(p.prayerId().toString());
        }

        @Test
        @DisplayName("기도제목 2개면 이틀 연속 같은 것이 나오지 않고 번갈아 나온다")
        void twoPrayersAlternate() {
            PrayerPushCandidate a = gathering("A", TODAY.minusDays(1));
            PrayerPushCandidate b = personal("B", TODAY.minusDays(2).atStartOfDay());

            List<PushMessage> sent = simulate(4, List.of(a, b));

            for (int i = 1; i < sent.size(); i++) {
                assertThat(sent.get(i).entityId()).isNotEqualTo(sent.get(i - 1).entityId());
            }
        }

        @Test
        @DisplayName("기도제목 3개면 세 개가 모두 한 번씩 나온 뒤에 반복된다")
        void threePrayersRotate() {
            List<PrayerPushCandidate> list = List.of(
                    gathering("A", TODAY.minusDays(1)),
                    gathering("B", TODAY.minusDays(2)),
                    personal("C", TODAY.minusDays(10).atStartOfDay()));

            List<PushMessage> sent = simulate(3, list);

            Set<String> shown = new HashSet<>();
            sent.forEach(m -> shown.add(m.entityId()));
            assertThat(shown).hasSize(3);
        }

        @Test
        @DisplayName("제목 템플릿은 이틀 연속 같지 않고, 5개를 모두 쓴 뒤에 반복된다")
        void templatesRotate() {
            PrayerPushCandidate p = personal("하나", TODAY.atStartOfDay());

            List<PushMessage> sent = simulate(5, List.of(p));

            Set<String> keys = new HashSet<>();
            for (int i = 0; i < sent.size(); i++) {
                keys.add(sent.get(i).templateKey());
                if (i > 0) assertThat(sent.get(i).templateKey()).isNotEqualTo(sent.get(i - 1).templateKey());
            }
            assertThat(keys).hasSize(5);
        }

        @Test
        @DisplayName("100자를 넘는 기도제목은 말줄임 처리한다")
        void longBodyTruncated() {
            PrayerPushCandidate p = personal("가".repeat(150), TODAY.atStartOfDay());

            PushMessage m = PrayerPushComposer.compose(TODAY, List.of(p), List.of());

            assertThat(m.body()).hasSize(101).endsWith("…");
        }

        @Test
        @DisplayName("7일 전 모임 기도제목은 이번 주(오늘 포함 7일)가 아니다")
        void sevenDaysAgoIsNotThisWeek() {
            PrayerPushCandidate p = gathering("지난주", TODAY.minusDays(7));

            PushMessage m = PrayerPushComposer.compose(TODAY, List.of(p), List.of());

            assertThat(PrayerPushComposer.groupOf(m.templateKey())).isNotEqualTo(Group.A);
        }
    }

    @Nested
    @DisplayName("B: 이번 주 기도제목도 나의 기도도 없을 때")
    class GroupB {

        @Test
        @DisplayName("2주 이상 지난 미응답 기도제목이 있으면 B-1으로 다시 꺼내고, 몇 주 전인지 보여준다")
        void oldPrayerB1() {
            PrayerPushCandidate old = gathering("동생이 교회에 다시 나오도록", TODAY.minusDays(21));

            PushMessage m = PrayerPushComposer.compose(TODAY, List.of(old), List.of());

            assertThat(m.templateKey()).isEqualTo("PRAYER_B1_1");
            assertThat(m.title()).isEqualTo("3주 전 기도제목, 기억나세요?");
            assertThat(m.body()).isEqualTo("동생이 교회에 다시 나오도록");
            assertThat(m.url()).isEqualTo("/prayers?highlight=" + old.prayerId());
        }

        @Test
        @DisplayName("B-1과 B-2를 하루씩 번갈아 보낸다")
        void alternateB1B2() {
            PrayerPushCandidate old = gathering("오래된 기도", TODAY.minusDays(30));

            List<PushMessage> sent = simulate(4, List.of(old));

            assertThat(sent).extracting(m -> PrayerPushComposer.groupOf(m.templateKey()))
                    .containsExactly(Group.B1, Group.B2, Group.B1, Group.B2);
        }

        @Test
        @DisplayName("1~2주 사이(7~13일 전) 기도제목은 B-1 대상이 아니므로 B-2를 보낸다")
        void betweenOneAndTwoWeeks() {
            PrayerPushCandidate p = gathering("10일 전", TODAY.minusDays(10));

            PushMessage m = PrayerPushComposer.compose(TODAY, List.of(p), List.of());

            assertThat(PrayerPushComposer.groupOf(m.templateKey())).isEqualTo(Group.B2);
        }

        @Test
        @DisplayName("꺼낼 기도제목이 없으면 B-2(적어보기)를 보내고 작성 화면으로 이동한다")
        void nothingB2() {
            PushMessage m = PrayerPushComposer.compose(TODAY, List.of(), List.of());

            assertThat(PrayerPushComposer.groupOf(m.templateKey())).isEqualTo(Group.B2);
            assertThat(m.body()).isNotBlank();
            assertThat(m.url()).isEqualTo("/prayers?write=1");
            assertThat(m.entityId()).isNull();
        }

        @Test
        @DisplayName("B-2만 계속 보내도 매일 문구가 바뀐다")
        void b2Rotates() {
            List<PushMessage> sent = simulate(5, List.of());

            Set<String> keys = new HashSet<>();
            sent.forEach(m -> keys.add(m.templateKey()));
            assertThat(keys).hasSize(5);
        }

        @Test
        @DisplayName("기존(PRAYER_LEGACY) 이력이 있어도 정상적으로 동작한다")
        void legacyHistory() {
            PushMessage m = PrayerPushComposer.compose(TODAY, List.of(),
                    List.of(log("PRAYER_LEGACY", null, TODAY.minusDays(1))));

            assertThat(PrayerPushComposer.groupOf(m.templateKey())).isEqualTo(Group.B2);
        }
    }

    @Test
    @DisplayName("이번 주 기도제목이 생기면 B에서 A로 바로 바뀐다")
    void switchesToAWhenPrayerAdded() {
        List<PushDeliveryLog> history = List.of(log("PRAYER_B2_1", null, TODAY.minusDays(1)));
        PrayerPushCandidate fresh = personal("새로 적은 기도", TODAY.atStartOfDay());

        PushMessage m = PrayerPushComposer.compose(TODAY, List.of(fresh), history);

        assertThat(PrayerPushComposer.groupOf(m.templateKey())).isEqualTo(Group.A);
        assertThat(m.entityId()).isEqualTo(fresh.prayerId().toString());
    }
}
