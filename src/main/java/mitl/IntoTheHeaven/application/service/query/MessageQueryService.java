package mitl.IntoTheHeaven.application.service.query;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.query.MessageQueryUseCase;
import mitl.IntoTheHeaven.application.port.out.MessagePort;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageQueryService implements MessageQueryUseCase {

    private final MessagePort messagePort;

    @Override
    public List<Message> getMyMessages(MemberId memberId) {
        return messagePort.findByReceiverId(memberId.getValue());
    }

    @Override
    public long getUnreadCount(MemberId memberId) {
        return messagePort.countUnreadByReceiverId(memberId.getValue());
    }

    @Override
    public List<Message> getSentMessages(MemberId memberId) {
        return messagePort.findBySenderId(memberId.getValue());
    }
}
