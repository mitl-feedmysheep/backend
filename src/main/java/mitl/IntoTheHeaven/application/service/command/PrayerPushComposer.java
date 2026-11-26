package mitl.IntoTheHeaven.application.service.command;

import mitl.IntoTheHeaven.application.port.out.PrayerPushCandidate;
import mitl.IntoTheHeaven.application.service.command.PushDispatchService.PushMessage;
import mitl.IntoTheHeaven.domain.enums.PushType;
import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 회원별 매일 기도 알림 내용을 결정한다 (순수 함수, 상태 없음).
 *
 * <pre>
 * 후보 = 이번 주(모임 날짜 기준 최근 7일) 모임 기도제목 + 나의 기도 (모두 미응답)
 * 후보 있음            → A: 가장 오래전에 노출한(또는 미노출) 기도제목
 * 후보 없음 + 2주 이상 지난 미응답 모임 기도제목 있음 → B-1 / B-2 하루씩 번갈아
 * 그 외               → B-2 (기도제목 적어보기)
 * 제목 템플릿은 어제와 같은 것을 피하고, 가장 오래전에 쓴 것부터 돌려 쓴다.
 * </pre>
 *
 * 노출 이력은 push_delivery_log(DAILY_PRAYER)의 template_key / entity_id로 판단한다.
 */
public final class PrayerPushComposer {

    /** 이번 주 = 오늘 포함 최근 7일 (모임 날짜 기준) */
    static final int THIS_WEEK_DAYS = 7;
    /** B-1 대상: 모임 날짜가 2주 이상 지난 미응답 기도제목 */
    static final int OLD_PRAYER_MIN_DAYS = 14;
    /** B-1 대상 최대 범위 (조회 범위와 동일) */
    public static final int OLD_PRAYER_MAX_DAYS = 90;
    static final int BODY_MAX_LENGTH = 100;

    static final String ENTITY_TYPE = "PRAYER";

    enum Group { A, B1, B2 }

    enum Template {
        PRAYER_A_1(Group.A, "오늘 이 기도로 하루를 열어요 🙏", null),
        PRAYER_A_2(Group.A, "오늘도 이 마음을 올려드려요", null),
        PRAYER_A_3(Group.A, "잠깐 멈추고 기도해요 🙏", null),
        PRAYER_A_4(Group.A, "하나님께 다시 한번 맡겨요", null),
        PRAYER_A_5(Group.A, "오늘 하루, 이 기도와 함께해요", null),

        PRAYER_B1_1(Group.B1, "%d주 전 기도제목, 기억나세요?", null),
        PRAYER_B1_2(Group.B1, "이 기도를 계속 이어가요 🙏", null),
        PRAYER_B1_3(Group.B1, "아직 기도하고 있는 제목이에요", null),

        PRAYER_B2_1(Group.B2, "이번 주 기도제목을 적어볼까요? ✍️", "마음에 있는 것 하나만 적고 기도해요"),
        PRAYER_B2_2(Group.B2, "요즘 어떤 마음이세요?", "짧게라도 기도제목으로 남겨봐요"),
        PRAYER_B2_3(Group.B2, "오늘 감사한 일 하나 있나요? 🌿", "감사도 기도가 돼요. 한 줄 적어봐요"),
        PRAYER_B2_4(Group.B2, "함께 기도하고 싶은 사람이 있나요?", "가족, 친구를 위한 기도제목을 적어봐요"),
        PRAYER_B2_5(Group.B2, "한 주를 기도로 시작해요", "이번 주 기도제목을 적고 기도하러 가요 🙏");

        final Group group;
        final String title;
        final String body;

        Template(Group group, String title, String body) {
            this.group = group;
            this.title = title;
            this.body = body;
        }

        static Optional<Template> fromKey(String key) {
            if (key == null) return Optional.empty();
            for (Template t : values()) {
                if (t.name().equals(key)) return Optional.of(t);
            }
            return Optional.empty();
        }
    }

    private PrayerPushComposer() {
    }

    /**
     * @param today      KST 기준 오늘
     * @param candidates 이 회원의 미응답 기도제목 (나의 기도 + 최근 {@value #OLD_PRAYER_MAX_DAYS}일 모임 기도제목)
     * @param history    이 회원의 과거 DAILY_PRAYER 발송 로그 (순서 무관)
     */
    public static PushMessage compose(LocalDate today, List<PrayerPushCandidate> candidates, List<PushDeliveryLog> history) {
        Map<String, LocalDateTime> lastShownByEntity = new HashMap<>();
        Map<String, LocalDateTime> lastUsedByTemplate = new HashMap<>();
        PushDeliveryLog latest = null;
        for (PushDeliveryLog log : history) {
            if (log.getSentAt() == null) continue;
            if (log.getEntityId() != null) lastShownByEntity.merge(log.getEntityId(), log.getSentAt(), PrayerPushComposer::max);
            if (log.getTemplateKey() != null) lastUsedByTemplate.merge(log.getTemplateKey(), log.getSentAt(), PrayerPushComposer::max);
            if (latest == null || log.getSentAt().isAfter(latest.getSentAt())) latest = log;
        }
        Template lastTemplate = latest == null ? null : Template.fromKey(latest.getTemplateKey()).orElse(null);
        Group lastGroup = lastTemplate == null ? null : lastTemplate.group;

        LocalDate weekStart = today.minusDays(THIS_WEEK_DAYS - 1L);
        LocalDate oldCutoff = today.minusDays(OLD_PRAYER_MIN_DAYS);

        List<PrayerPushCandidate> thisWeek = candidates.stream()
                .filter(c -> c.personal() || (c.gatheringDate() != null && !c.gatheringDate().isBefore(weekStart)))
                .toList();

        if (!thisWeek.isEmpty()) {
            PrayerPushCandidate prayer = leastRecentlyShown(thisWeek, lastShownByEntity);
            Template t = pickTemplate(Group.A, lastTemplate, lastUsedByTemplate);
            return prayerMessage(t, t.title, prayer);
        }

        List<PrayerPushCandidate> old = candidates.stream()
                .filter(c -> !c.personal() && c.gatheringDate() != null && !c.gatheringDate().isAfter(oldCutoff))
                .toList();

        if (!old.isEmpty() && lastGroup != Group.B1) {
            PrayerPushCandidate prayer = leastRecentlyShown(old, lastShownByEntity);
            Template t = pickTemplate(Group.B1, lastTemplate, lastUsedByTemplate);
            long weeks = Math.max(2, ChronoUnit.DAYS.between(prayer.gatheringDate(), today) / 7);
            String title = t.title.contains("%d") ? String.format(t.title, weeks) : t.title;
            return prayerMessage(t, title, prayer);
        }

        Template t = pickTemplate(Group.B2, lastTemplate, lastUsedByTemplate);
        return PushMessage.of(PushType.DAILY_PRAYER, t.title, t.body, "/prayers?write=1")
                .withTemplate(t.name());
    }

    private static PushMessage prayerMessage(Template t, String title, PrayerPushCandidate prayer) {
        String id = prayer.prayerId().toString();
        return PushMessage.of(PushType.DAILY_PRAYER, title, truncate(prayer.prayerRequest()), "/prayers?highlight=" + id)
                .withTemplate(t.name())
                .withEntity(ENTITY_TYPE, id);
    }

    /** 노출 이력이 없는 것 우선, 그다음 가장 오래전에 노출한 것. 동률이면 최신 기도제목 우선 */
    private static PrayerPushCandidate leastRecentlyShown(List<PrayerPushCandidate> list, Map<String, LocalDateTime> lastShown) {
        Comparator<PrayerPushCandidate> byLastShown = Comparator.comparing(
                (PrayerPushCandidate c) -> lastShown.get(c.prayerId().toString()),
                Comparator.nullsFirst(Comparator.naturalOrder()));
        Comparator<PrayerPushCandidate> newerFirst = Comparator.comparing(
                PrayerPushComposer::recency, Comparator.nullsLast(Comparator.reverseOrder()));
        return list.stream()
                .min(byLastShown.thenComparing(newerFirst).thenComparing(c -> c.prayerId().toString()))
                .orElseThrow();
    }

    private static LocalDateTime recency(PrayerPushCandidate c) {
        if (c.gatheringDate() != null) return c.gatheringDate().atStartOfDay();
        return c.createdAt();
    }

    /** 어제(직전 발송) 템플릿을 제외하고, 가장 오래전에 쓴(또는 안 쓴) 템플릿. 동률이면 선언 순서 */
    private static Template pickTemplate(Group group, Template lastTemplate, Map<String, LocalDateTime> lastUsed) {
        List<Template> pool = Arrays.stream(Template.values())
                .filter(t -> t.group == group)
                .filter(t -> t != lastTemplate)
                .toList();
        return pool.stream()
                .min(Comparator.comparing((Template t) -> lastUsed.get(t.name()),
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow();
    }

    private static String truncate(String text) {
        String trimmed = Objects.requireNonNullElse(text, "").trim();
        return trimmed.length() > BODY_MAX_LENGTH ? trimmed.substring(0, BODY_MAX_LENGTH) + "…" : trimmed;
    }

    private static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
        return a.isAfter(b) ? a : b;
    }

    /** 테스트/로깅용 */
    static Group groupOf(String templateKey) {
        return Template.fromKey(templateKey).map(t -> t.group).orElse(null);
    }
}
