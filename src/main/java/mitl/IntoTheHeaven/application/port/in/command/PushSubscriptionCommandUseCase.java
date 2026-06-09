package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.SubscribePushCommand;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.PushSubscription;

public interface PushSubscriptionCommandUseCase {

    PushSubscription subscribe(SubscribePushCommand command);

    void unsubscribe(MemberId memberId, String endpoint);
}
