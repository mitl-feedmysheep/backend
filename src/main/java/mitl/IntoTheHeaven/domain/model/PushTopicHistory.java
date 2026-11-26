package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.PushTopic;
import mitl.IntoTheHeaven.domain.enums.PushTopicAction;
import mitl.IntoTheHeaven.domain.enums.PushTopicChangeSource;

import java.util.UUID;

@Getter
@Builder
public class PushTopicHistory {

    private final UUID id;
    private final MemberId memberId;
    private final PushTopic topic;
    private final PushTopicAction action;
    private final PushTopicChangeSource source;

    public static PushTopicHistory of(MemberId memberId, PushTopic topic, PushTopicAction action, PushTopicChangeSource source) {
        return PushTopicHistory.builder()
                .id(UUID.randomUUID())
                .memberId(memberId)
                .topic(topic)
                .action(action)
                .source(source)
                .build();
    }
}
