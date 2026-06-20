package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;

public interface PushSubscriptionTopicCommandUseCase {

    void subscribe(MemberId memberId, PushTopic topic);

    void unsubscribe(MemberId memberId, PushTopic topic);

    List<PushTopic> getSubscribedTopics(MemberId memberId);
}
