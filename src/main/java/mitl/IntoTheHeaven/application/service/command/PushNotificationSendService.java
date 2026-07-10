package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationSendService {

    private static final int POOL_SIZE = 8;
    private static final int SEND_TIMEOUT_SECONDS = 10;
    private static final String NOTIFICATION_TITLE = "오늘도 기도로 하루를 열어요 🙏";
    private static final String DEEPLINK_URL = "/";
    private static final PushPayload PRAYER_PAYLOAD = new PushPayload(NOTIFICATION_TITLE, null, DEEPLINK_URL);

    private final PushSubscriptionPort pushSubscriptionPort;
    private final PushSubscriptionTopicPort pushSubscriptionTopicPort;
    private final WebPushPort webPushPort;

    public void sendDailyPrayerPush() {
        List<MemberId> prayerTopicMembers = pushSubscriptionTopicPort.findMemberIdsByTopic(PushTopic.PRAYER);
        if (prayerTopicMembers.isEmpty()) return;

        List<PushSubscription> allSubscriptions = pushSubscriptionPort.findByMemberIds(prayerTopicMembers);
        if (allSubscriptions.isEmpty()) return;

        Map<UUID, List<PushSubscription>> byMember = groupByMember(allSubscriptions);

        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<UUID, List<PushSubscription>> entry : byMember.entrySet()) {
            List<PushSubscription> subs = entry.getValue();
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    for (PushSubscription sub : subs) {
                        SendResult result = webPushPort.send(sub, PRAYER_PAYLOAD);
                        handleResult(result, sub.getEndpoint());
                    }
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

    private void handleResult(SendResult result, String endpoint) {
        switch (result) {
            case GONE, INVALID -> {
                log.info("Removing expired subscription: {}", endpoint);
                pushSubscriptionPort.deleteByEndpoint(endpoint);
            }
            case TRANSIENT_FAIL -> log.warn("Transient push failure, will retry tomorrow: {}", endpoint);
            case SUCCESS -> {}
        }
    }
}
