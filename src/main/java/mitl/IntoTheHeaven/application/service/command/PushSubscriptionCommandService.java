package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PushSubscriptionCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.SubscribePushCommand;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class PushSubscriptionCommandService implements PushSubscriptionCommandUseCase {

    private final PushSubscriptionPort pushSubscriptionPort;

    @Override
    @Transactional
    public PushSubscription subscribe(SubscribePushCommand command) {
        validateTimezone(command.getTimezone());

        // upsert: 같은 endpoint가 이미 있으면 삭제 후 재등록
        if (pushSubscriptionPort.existsByEndpoint(command.getEndpoint())) {
            pushSubscriptionPort.deleteByEndpoint(command.getEndpoint());
        }

        PushSubscription subscription = PushSubscription.of(
                command.getMemberId(),
                command.getEndpoint(),
                command.getP256dh(),
                command.getAuth(),
                command.getUserAgent(),
                command.getTimezone()
        );

        return pushSubscriptionPort.save(subscription);
    }

    @Override
    @Transactional
    public void unsubscribe(MemberId memberId, String endpoint) {
        pushSubscriptionPort.deleteByEndpoint(endpoint);
    }

    private void validateTimezone(String timezone) {
        if (timezone == null || timezone.isBlank()) return;
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timezone: " + timezone);
        }
    }
}
