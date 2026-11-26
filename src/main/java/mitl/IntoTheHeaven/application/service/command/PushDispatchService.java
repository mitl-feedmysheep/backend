package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.out.PushDeliveryLogPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.PushPayload;
import mitl.IntoTheHeaven.application.port.out.WebPushPort.SendResult;
import mitl.IntoTheHeaven.domain.enums.PushType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 * 모든 푸시 발송의 단일 진입점.
 * 발송 → 결과 로그(push_delivery_log) 저장 → 만료 구독 정리를 일관되게 처리한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PushDispatchService {

    /** 앱 측 클릭 수집용 쿼리 파라미터 (web-app에서 읽고 제거) */
    static final String DELIVERY_QUERY_PARAM = "pd";

    private final WebPushPort webPushPort;
    private final PushSubscriptionPort pushSubscriptionPort;
    private final PushDeliveryLogPort pushDeliveryLogPort;

    /**
     * @param type        알림 종류
     * @param templateKey 문구 템플릿 키 (없으면 null)
     * @param entityType  알림이 가리키는 엔티티 종류 (없으면 null)
     * @param entityId    알림이 가리키는 엔티티 ID (없으면 null)
     */
    public record PushMessage(PushType type, String title, String body, String url,
                              String templateKey, String entityType, String entityId) {
        public static PushMessage of(PushType type, String title, String body, String url) {
            return new PushMessage(type, title, body, url, null, null, null);
        }

        public PushMessage withEntity(String entityType, String entityId) {
            return new PushMessage(type, title, body, url, templateKey, entityType, entityId);
        }

        public PushMessage withTemplate(String templateKey) {
            return new PushMessage(type, title, body, url, templateKey, entityType, entityId);
        }
    }

    public void sendToMembers(List<MemberId> memberIds, PushMessage message) {
        if (memberIds == null || memberIds.isEmpty()) return;
        sendToSubscriptions(pushSubscriptionPort.findByMemberIds(memberIds), message);
    }

    public void sendToMember(MemberId memberId, PushMessage message) {
        sendToSubscriptions(pushSubscriptionPort.findByMemberId(memberId), message);
    }

    public void sendToSubscriptions(List<PushSubscription> subscriptions, PushMessage message) {
        if (subscriptions == null) return;
        for (PushSubscription sub : subscriptions) {
            send(sub, message);
        }
    }

    public SendResult send(PushSubscription subscription, PushMessage message) {
        UUID deliveryId = UUID.randomUUID();
        PushPayload payload = new PushPayload(
                message.title(),
                message.body(),
                appendDeliveryParam(message.url(), deliveryId),
                deliveryId.toString(),
                message.type().name()
        );

        SendResult result = webPushPort.send(subscription, payload);
        recordDelivery(deliveryId, subscription, message, result);

        switch (result) {
            case GONE, INVALID -> {
                log.info("Removing expired subscription: {}", subscription.getEndpoint());
                pushSubscriptionPort.deleteByEndpoint(subscription.getEndpoint());
            }
            case TRANSIENT_FAIL -> log.warn("Transient push failure ({}): {}", message.type(), subscription.getEndpoint());
            case SUCCESS -> {
            }
        }
        return result;
    }

    private void recordDelivery(UUID deliveryId, PushSubscription subscription, PushMessage message, SendResult result) {
        try {
            pushDeliveryLogPort.save(PushDeliveryLog.builder()
                    .id(deliveryId)
                    .memberId(subscription.getMemberId())
                    .subscriptionId(subscription.getId() != null ? subscription.getId().getValue() : null)
                    .pushType(message.type())
                    .templateKey(message.templateKey())
                    .entityType(message.entityType())
                    .entityId(message.entityId())
                    .sendResult(result.name())
                    .sentAt(LocalDateTime.now(ZoneOffset.UTC))
                    .build());
        } catch (Exception e) {
            // 로그 저장 실패가 발송 흐름을 막지 않도록 한다
            log.warn("Failed to record push delivery {}: {}", deliveryId, e.getMessage());
        }
    }

    static String appendDeliveryParam(String url, UUID deliveryId) {
        String base = (url == null || url.isBlank()) ? "/" : url;
        String fragment = "";
        int hashIdx = base.indexOf('#');
        if (hashIdx >= 0) {
            fragment = base.substring(hashIdx);
            base = base.substring(0, hashIdx);
        }
        String sep = base.contains("?") ? "&" : "?";
        return base + sep + DELIVERY_QUERY_PARAM + "=" + URLEncoder.encode(deliveryId.toString(), StandardCharsets.UTF_8) + fragment;
    }
}
