package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.MessageCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.SendMessageCommand;
import mitl.IntoTheHeaven.application.port.out.MessagePort;
import mitl.IntoTheHeaven.domain.model.Message;
import mitl.IntoTheHeaven.domain.model.MessageId;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageCommandService implements MessageCommandUseCase {

    private final MessagePort messagePort;

    @Override
    public Message sendMessage(SendMessageCommand command) {
        Message message = Message.builder()
                .id(MessageId.from(UUID.randomUUID()))
                .senderId(command.getSenderId())
                .receiverId(command.getReceiverId())
                .content(command.getContent())
                .type(command.getType())
                .isRead(false)
                .build();

        return messagePort.save(message);
    }

    @Override
    public void markAsRead(UUID messageId, MemberId memberId) {
        Message message = messagePort.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        if (!message.getReceiverId().equals(memberId)) {
            throw new IllegalArgumentException("Only the receiver can mark the message as read");
        }

        messagePort.markAsRead(messageId);
    }
}
