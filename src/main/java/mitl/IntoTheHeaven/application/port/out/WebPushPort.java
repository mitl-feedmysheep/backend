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

    record PushPayload(String title, String body, String url) {
    }
}
