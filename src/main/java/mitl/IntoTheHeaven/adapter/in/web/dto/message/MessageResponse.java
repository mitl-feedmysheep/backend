package mitl.IntoTheHeaven.adapter.in.web.dto.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class MessageResponse {
    private final UUID id;
    private final String senderName;
    private final String receiverName;
    private final String content;
    private final String type;
    @JsonProperty("isRead")
    private final boolean isRead;
    private final LocalDateTime createdAt;

    @Builder
    public MessageResponse(UUID id, String senderName, String receiverName, String content, String type, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.content = content;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static MessageResponse from(Message message) {
        return MessageResponse.builder()
                .id(message.getId().getValue())
                .senderName(message.getSenderName())
                .receiverName(message.getReceiverName())
                .content(message.getContent())
                .type(message.getType() != null ? message.getType().getValue() : null)
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }

    public static List<MessageResponse> from(List<Message> messages) {
        return messages.stream()
                .map(MessageResponse::from)
                .toList();
    }
}
