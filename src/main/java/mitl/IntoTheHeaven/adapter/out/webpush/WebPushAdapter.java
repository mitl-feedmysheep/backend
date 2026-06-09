package mitl.IntoTheHeaven.adapter.out.webpush;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mitl.IntoTheHeaven.application.port.out.WebPushPort;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Subscription;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebPushAdapter implements WebPushPort {

    private final PushService pushService;
    private final ObjectMapper objectMapper;

    @Override
    public SendResult send(PushSubscription subscription, PushPayload payload) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "title", payload.title(),
                    "body", payload.body(),
                    "url", payload.url()
            ));

            Subscription sub = new Subscription(
                    subscription.getEndpoint(),
                    new Subscription.Keys(subscription.getP256dh(), subscription.getAuth())
            );

            Notification notification = new Notification(sub, json);
            HttpResponse response = pushService.send(notification);
            int status = response.getStatusLine().getStatusCode();

            if (status >= 200 && status < 300) {
                return SendResult.SUCCESS;
            } else if (status == 410 || status == 404) {
                log.info("Push subscription gone ({}): {}", status, subscription.getEndpoint());
                return SendResult.GONE;
            } else if (status == 403) {
                log.warn("Push VAPID mismatch (403): {}", subscription.getEndpoint());
                return SendResult.GONE;
            } else if (status >= 400 && status < 500) {
                log.warn("Push invalid subscription ({}): {}", status, subscription.getEndpoint());
                return SendResult.INVALID;
            } else {
                log.warn("Push transient failure ({}): {}", status, subscription.getEndpoint());
                return SendResult.TRANSIENT_FAIL;
            }
        } catch (Exception e) {
            log.error("Push send error for endpoint {}: {}", subscription.getEndpoint(), e.getMessage());
            return SendResult.TRANSIENT_FAIL;
        }
    }
}
