package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PushDeliveryCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.PushDeliveryLogPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PushDeliveryCommandService implements PushDeliveryCommandUseCase {

    private final PushDeliveryLogPort pushDeliveryLogPort;

    @Override
    public void recordClick(UUID deliveryId) {
        pushDeliveryLogPort.markClicked(deliveryId, LocalDateTime.now(ZoneOffset.UTC));
    }
}
