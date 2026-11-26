package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.model.PushDeliveryLog;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PushDeliveryLogPort {

    void save(PushDeliveryLog log);

    /**
     * 최초 클릭만 반영한다. 이미 클릭됐거나 존재하지 않으면 false.
     */
    boolean markClicked(UUID deliveryId, LocalDateTime clickedAt);
}
