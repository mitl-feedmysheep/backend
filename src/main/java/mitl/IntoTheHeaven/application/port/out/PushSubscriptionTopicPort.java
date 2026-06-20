package mitl.IntoTheHeaven.application.port.out;

import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.model.MemberId;

import java.util.List;

public interface PushSubscriptionTopicPort {

    void subscribe(MemberId memberId, PushTopic topic);

    void unsubscribe(MemberId memberId, PushTopic topic);

    boolean isSubscribed(MemberId memberId, PushTopic topic);

    List<PushTopic> findTopicsByMemberId(MemberId memberId);

    List<MemberId> findMemberIdsByTopic(PushTopic topic);

    void deleteAllByMemberId(MemberId memberId);
}
