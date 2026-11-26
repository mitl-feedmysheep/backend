package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PushSubscriptionTopicCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.application.port.out.PushTopicHistoryPort;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.enums.PushTopicAction;
import mitl.IntoTheHeaven.domain.enums.PushTopicChangeSource;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushTopicHistory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PushSubscriptionTopicCommandService implements PushSubscriptionTopicCommandUseCase {

    private final PushSubscriptionTopicPort pushSubscriptionTopicPort;
    private final PushTopicHistoryPort pushTopicHistoryPort;

    @Override
    @Transactional
    public void subscribe(MemberId memberId, PushTopic topic) {
        if (pushSubscriptionTopicPort.isSubscribed(memberId, topic)) return;
        pushSubscriptionTopicPort.subscribe(memberId, topic);
        pushTopicHistoryPort.save(PushTopicHistory.of(memberId, topic, PushTopicAction.SUBSCRIBE, PushTopicChangeSource.USER_TOGGLE));
    }

    @Override
    @Transactional
    public void unsubscribe(MemberId memberId, PushTopic topic) {
        if (!pushSubscriptionTopicPort.isSubscribed(memberId, topic)) return;
        pushSubscriptionTopicPort.unsubscribe(memberId, topic);
        pushTopicHistoryPort.save(PushTopicHistory.of(memberId, topic, PushTopicAction.UNSUBSCRIBE, PushTopicChangeSource.USER_TOGGLE));
    }

    @Override
    public List<PushTopic> getSubscribedTopics(MemberId memberId) {
        return pushSubscriptionTopicPort.findTopicsByMemberId(memberId);
    }
}
