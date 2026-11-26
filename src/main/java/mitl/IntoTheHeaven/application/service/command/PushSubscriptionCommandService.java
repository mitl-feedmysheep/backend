package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PushSubscriptionCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.SubscribePushCommand;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionPort;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.port.out.PushTopicHistoryPort;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.enums.PushTopicAction;
import mitl.IntoTheHeaven.domain.enums.PushTopicChangeSource;
import mitl.IntoTheHeaven.domain.model.PushTopicHistory;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PushSubscriptionCommandService implements PushSubscriptionCommandUseCase {

    private final PushSubscriptionPort pushSubscriptionPort;
    private final PushSubscriptionTopicPort pushSubscriptionTopicPort;
    private final PushTopicHistoryPort pushTopicHistoryPort;

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

        // 다른 기기 구독이 남아 있으면 토픽 설정을 유지한다 (기기 1대 해지로 전체 알림이 꺼지던 버그 수정)
        if (!pushSubscriptionPort.findByMemberId(memberId).isEmpty()) return;

        List<PushTopic> topics = pushSubscriptionTopicPort.findTopicsByMemberId(memberId);
        if (topics.isEmpty()) return;

        pushSubscriptionTopicPort.deleteAllByMemberId(memberId);
        for (PushTopic topic : topics) {
            pushTopicHistoryPort.save(PushTopicHistory.of(
                    memberId, topic, PushTopicAction.UNSUBSCRIBE, PushTopicChangeSource.ALL_DEVICES_REMOVED));
        }
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
