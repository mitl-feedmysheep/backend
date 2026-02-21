package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.MessageType;
import mitl.IntoTheHeaven.domain.model.MemberId;

@Getter
@AllArgsConstructor
public class SendMessageCommand {
    private final MemberId senderId;
    private final MemberId receiverId;
    private final String content;
    private final MessageType type;
}
