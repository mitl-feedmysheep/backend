package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.PushType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 푸시 발송 1건(기기 단위) 기록. id는 payload의 deliveryId로 전달되어 클릭 추적에 사용된다.
 */
@Getter
@Builder
public class PushDeliveryLog {

    private final UUID id;
    private final MemberId memberId;
    private final UUID subscriptionId;
    private final PushType pushType;
    private final String templateKey;
    private final String entityType;
    private final String entityId;
    private final String sendResult;
    private final LocalDateTime sentAt;
    private final LocalDateTime clickedAt;
}
