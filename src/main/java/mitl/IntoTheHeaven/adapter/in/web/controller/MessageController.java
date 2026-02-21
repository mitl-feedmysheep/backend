package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.message.MessageResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.message.SendMessageRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.message.UnreadCountResponse;
import mitl.IntoTheHeaven.application.port.in.command.MessageCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.SendMessageCommand;
import mitl.IntoTheHeaven.application.port.in.query.MessageQueryUseCase;
import mitl.IntoTheHeaven.domain.enums.MessageType;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Message", description = "APIs for Message Management")
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageCommandUseCase messageCommandUseCase;
    private final MessageQueryUseCase messageQueryUseCase;

    @Operation(summary = "Send Message", description = "Sends a congratulatory message to a member.")
    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal String memberId,
            @RequestBody @Valid SendMessageRequest request) {
        SendMessageCommand command = new SendMessageCommand(
                MemberId.from(UUID.fromString(memberId)),
                MemberId.from(request.getReceiverId()),
                request.getContent(),
                MessageType.valueOf(request.getType()));
        Message message = messageCommandUseCase.sendMessage(command);
        return ResponseEntity.ok(MessageResponse.from(message));
    }

    @Operation(summary = "Get My Messages", description = "Retrieves all messages received by the current user.")
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMyMessages(
            @AuthenticationPrincipal String memberId) {
        List<Message> messages = messageQueryUseCase.getMyMessages(
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok(MessageResponse.from(messages));
    }

    @Operation(summary = "Get Sent Messages", description = "Retrieves all messages sent by the current user.")
    @GetMapping("/sent")
    public ResponseEntity<List<MessageResponse>> getSentMessages(
            @AuthenticationPrincipal String memberId) {
        List<Message> messages = messageQueryUseCase.getSentMessages(
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok(MessageResponse.from(messages));
    }

    @Operation(summary = "Get Unread Count", description = "Returns the number of unread messages for the current user.")
    @GetMapping("/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal String memberId) {
        long count = messageQueryUseCase.getUnreadCount(
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @Operation(summary = "Mark as Read", description = "Marks a specific message as read.")
    @PatchMapping("/{messageId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable("messageId") UUID messageId,
            @AuthenticationPrincipal String memberId) {
        messageCommandUseCase.markAsRead(messageId,
                MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok().build();
    }
}
