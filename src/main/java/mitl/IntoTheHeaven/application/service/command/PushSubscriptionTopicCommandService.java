package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PushSubscriptionTopicCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.PushSubscriptionTopicPort;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PushSubscriptionTopicCommandService implements PushSubscriptionTopicCommandUseCase {

    private final PushSubscriptionTopicPort pushSubscriptionTopicPort;

    @Override
    public void subscribe(MemberId memberId, PushTopic topic) {
        pushSubscriptionTopicPort.subscribe(memberId, topic);
    }

    @Override
    public void unsubscribe(MemberId memberId, PushTopic topic) {
        pushSubscriptionTopicPort.unsubscribe(memberId, topic);
    }

    @Override
    public List<PushTopic> getSubscribedTopics(MemberId memberId) {
        return pushSubscriptionTopicPort.findTopicsByMemberId(memberId);
    }
}
