package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;

import mitl.IntoTheHeaven.domain.enums.PushType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PushDeliveryLogPort {

    void save(PushDeliveryLog log);

    /** 회원들의 특정 종류 발송 로그 (since 이후, UTC) */
    List<PushDeliveryLog> findByMemberIdsAndTypeSince(List<UUID> memberIds, PushType type, LocalDateTime since);

    /**
     * 최초 클릭만 반영한다. 이미 클릭됐거나 존재하지 않으면 false.
     */
    boolean markClicked(UUID deliveryId, LocalDateTime clickedAt);
}
