package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PushSubscription {

    private final PushSubscriptionId id;
    private final MemberId memberId;
    private final String endpoint;
    private final String p256dh;
    private final String auth;
    private final String userAgent;
    private final String timezone;

    public static PushSubscription of(MemberId memberId, String endpoint, String p256dh, String auth,
                                      String userAgent, String timezone) {
        return PushSubscription.builder()
                .id(PushSubscriptionId.newId())
                .memberId(memberId)
                .endpoint(endpoint)
                .p256dh(p256dh)
                .auth(auth)
                .userAgent(userAgent)
                .timezone(timezone != null ? timezone : "Asia/Seoul")
                .build();
    }
}
