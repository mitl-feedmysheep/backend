package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.PushSubscription;

public interface WebPushPort {

    SendResult send(PushSubscription subscription, PushPayload payload);

    enum SendResult {
        SUCCESS,
        GONE,
        INVALID,
        TRANSIENT_FAIL
    }

    /**
     * @param deliveryId push_delivery_log ID (클릭 추적용, 없으면 null)
     * @param type       PushType 이름 (서비스워커 tag 결정용, 없으면 null)
     */
    record PushPayload(String title, String body, String url, String deliveryId, String type) {
        public PushPayload(String title, String body, String url) {
            this(title, body, url, null, null);
        }
    }
}
