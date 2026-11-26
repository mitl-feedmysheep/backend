package mitl.IntoTheHeaven.application.port.in.command;

import java.util.UUID;

public interface PushDeliveryCommandUseCase {

    /**
     * 푸시 알림 클릭 기록 (최초 클릭만 반영, 없는 ID는 무시)
     */
    void recordClick(UUID deliveryId);
}
