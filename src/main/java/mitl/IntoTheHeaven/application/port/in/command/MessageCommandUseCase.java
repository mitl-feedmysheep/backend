package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.SendMessageCommand;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Message;

import java.util.UUID;

public interface MessageCommandUseCase {

    Message sendMessage(SendMessageCommand command);

    void markAsRead(UUID messageId, MemberId memberId);
}
