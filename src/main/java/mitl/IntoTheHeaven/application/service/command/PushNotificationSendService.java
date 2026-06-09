package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.application.service.query.WeeklyPrayerQueryService;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private static final String NOTIFICATION_TITLE = "오늘도, 기도로 시작해볼까요? 🙏";
    private static final String DEEPLINK_URL = "/prayers";
    private static final int PREVIEW_MAX_LENGTH = 18;

    private final PushSubscriptionPort pushSubscriptionPort;
    private final WebPushPort webPushPort;
    private final WeeklyPrayerQueryService weeklyPrayerQueryService;
    private final Clock clock;

    public void sendDailyPrayerPush() {
        List<PushSubscription> allSubscriptions = pushSubscriptionPort.findAll();
        if (allSubscriptions.isEmpty()) return;

        List<PushSubscription> targeted = filterByCurrentHour(allSubscriptions);
        if (targeted.isEmpty()) return;

        Map<UUID, List<PushSubscription>> byMember = groupByMember(targeted);

        ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (Map.Entry<UUID, List<PushSubscription>> entry : byMember.entrySet()) {
            UUID memberUuid = entry.getKey();
            List<PushSubscription> subs = entry.getValue();
            ZoneId userZone = resolveZone(subs.get(0).getTimezone());

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    var memberId = mitl.IntoTheHeaven.domain.model.MemberId.from(memberUuid);
                    List<Prayer> prayers = weeklyPrayerQueryService.getWeeklyPrayers(memberId, userZone);

                    if (prayers.isEmpty()) return;

                    PushPayload payload = buildPayload(prayers);
                    for (PushSubscription sub : subs) {
                        SendResult result = webPushPort.send(sub, payload);
                        handleResult(result, sub.getEndpoint());
                    }
                } catch (Exception e) {
                    log.error("Error sending push for member {}: {}", memberUuid, e.getMessage());
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

    private List<PushSubscription> filterByCurrentHour(List<PushSubscription> subscriptions) {
        return subscriptions.stream()
                .filter(sub -> {
                    ZoneId zone = resolveZone(sub.getTimezone());
                    return ZonedDateTime.now(clock).withZoneSameInstant(zone).getHour() == 9;
                })
                .toList();
    }

    private Map<UUID, List<PushSubscription>> groupByMember(List<PushSubscription> subscriptions) {
        Map<UUID, List<PushSubscription>> map = new LinkedHashMap<>();
        for (PushSubscription sub : subscriptions) {
            map.computeIfAbsent(sub.getMemberId().getValue(), k -> new ArrayList<>()).add(sub);
        }
        return map;
    }

    private PushPayload buildPayload(List<Prayer> prayers) {
        String firstRequest = prayers.get(0).getPrayerRequest();
        String preview = firstRequest.length() > PREVIEW_MAX_LENGTH
                ? firstRequest.substring(0, PREVIEW_MAX_LENGTH) + "..."
                : firstRequest;

        String body = prayers.size() == 1
                ? "\"" + preview + "\""
                : "\"" + preview + "\" 외 " + (prayers.size() - 1) + "개";

        return new PushPayload(NOTIFICATION_TITLE, body, DEEPLINK_URL);
    }

    private void handleResult(SendResult result, String endpoint) {
        switch (result) {
            case GONE, INVALID -> {
                log.info("Removing expired subscription: {}", endpoint);
                pushSubscriptionPort.deleteByEndpoint(endpoint);
            }
            case TRANSIENT_FAIL -> log.warn("Transient push failure, will retry next cycle: {}", endpoint);
            case SUCCESS -> {}
        }
    }

    private ZoneId resolveZone(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            return ZoneId.of("Asia/Seoul");
        }
    }
}
