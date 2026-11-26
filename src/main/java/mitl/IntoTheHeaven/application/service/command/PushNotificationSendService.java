package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.out.PrayerPort;
import mitl.IntoTheHeaven.application.port.out.PrayerPushCandidate;
import mitl.IntoTheHeaven.application.port.out.PushDeliveryLogPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.service.command.PushDispatchService.PushMessage;
import mitl.IntoTheHeaven.domain.enums.PushType;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationSendService {

    private static final int POOL_SIZE = 8;
    private static final int SEND_TIMEOUT_SECONDS = 10;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    /** 기도제목/문구 로테이션 판단에 쓰는 과거 발송 이력 범위 */
    private static final int HISTORY_DAYS = 60;

    private final PushSubscriptionPort pushSubscriptionPort;
    private final PushSubscriptionTopicPort pushSubscriptionTopicPort;
    private final PushDispatchService pushDispatchService;
    private final PrayerPort prayerPort;
    private final PushDeliveryLogPort pushDeliveryLogPort;

    public void sendDailyPrayerPush() {
        List<MemberId> prayerTopicMembers = pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.PRAYER);
        if (prayerTopicMembers.isEmpty()) return;

        List<PushSubscription> allSubscriptions = pushSubscriptionPort.findByMemberIds(prayerTopicMembers);
        if (allSubscriptions.isEmpty()) return;

        Map<UUID, List<PushSubscription>> byMember = groupByMember(allSubscriptions);
        List<UUID> memberIds = new ArrayList<>(byMember.keySet());

        // 회원별 내용 결정에 필요한 데이터는 일괄 조회 (N+1 방지)
        LocalDate today = LocalDate.now(KST);
        Map<UUID, List<PrayerPushCandidate>> candidatesByMember = prayerPort
                .findUnansweredForPush(memberIds, today.minusDays(PrayerPushComposer.OLD_PRAYER_MAX_DAYS))
                .stream()
                .collect(Collectors.groupingBy(PrayerPushCandidate::memberId));
        Map<UUID, List<PushDeliveryLog>> historyByMember = pushDeliveryLogPort
                .findByMemberIdsAndTypeSince(memberIds, PushType.DAILY_PRAYER,
                        LocalDateTime.now(ZoneOffset.UTC).minusDays(HISTORY_DAYS))
                .stream()
                .collect(Collectors.groupingBy(l -> l.getMemberId().getValue()));

        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<UUID, List<PushSubscription>> entry : byMember.entrySet()) {
            List<PushSubscription> subs = entry.getValue();
            PushMessage message = PrayerPushComposer.compose(
                    today,
                    candidatesByMember.getOrDefault(entry.getKey(), List.of()),
                    historyByMember.getOrDefault(entry.getKey(), List.of()));
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    pushDispatchService.sendToSubscriptions(subs, message);
                } catch (Exception e) {
                    log.error("Error sending push for member {}: {}", entry.getKey(), e.getMessage());
                }
            }, executor);
            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .get(SEND_TIMEOUT_SECONDS * byMember.size(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Push send batch timed out or failed: {}", e.getMessage());
        } finally {
            executor.shutdown();
        }

        log.info("Daily prayer push completed: {} members targeted", byMember.size());
    }

    private Map<UUID, List<PushSubscription>> groupByMember(List<PushSubscription> subscriptions) {
        Map<UUID, List<PushSubscription>> map = new LinkedHashMap<>();
        for (PushSubscription sub : subscriptions) {
            map.computeIfAbsent(sub.getMemberId().getValue(), k -> new ArrayList<>()).add(sub);
        }
        return map;
    }
}
