package mitl.IntoTheHeaven.application.port.in.query;

import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Message;

import java.util.List;

public interface MessageQueryUseCase {

    List<Message> getMyMessages(MemberId memberId);

    long getUnreadCount(MemberId memberId);

    List<Message> getSentMessages(MemberId memberId);
}
