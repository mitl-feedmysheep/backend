package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.push.SubscribePushRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.push.UnsubscribePushRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.push.VapidPublicKeyResponse;
import mitl.IntoTheHeaven.application.port.in.command.PushSubscriptionCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.dto.SubscribePushCommand;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.global.config.WebPushConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Push", description = "Web Push Subscription APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/push")
public class PushSubscriptionController {

    private final PushSubscriptionCommandUseCase pushSubscriptionCommandUseCase;
    private final WebPushConfig webPushConfig;

    @Operation(summary = "Get VAPID Public Key")
    @GetMapping("/vapid-public-key")
    public ResponseEntity<VapidPublicKeyResponse> getVapidPublicKey() {
        return ResponseEntity.ok(new VapidPublicKeyResponse(webPushConfig.getPublicKey()));
    }

    @Operation(summary = "Subscribe to Push Notifications")
    @PostMapping("/subscriptions")
    public ResponseEntity<Void> subscribe(
            @AuthenticationPrincipal String memberId,
            @Valid @RequestBody SubscribePushRequest request
    ) {
        SubscribePushCommand command = SubscribePushCommand.builder()
                .memberId(MemberId.from(UUID.fromString(memberId)))
                .endpoint(request.getEndpoint())
                .p256dh(request.getP256dh())
                .auth(request.getAuth())
                .userAgent(request.getUserAgent())
                .timezone(request.getTimezone())
                .build();
        pushSubscriptionCommandUseCase.subscribe(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Unsubscribe from Push Notifications")
    @DeleteMapping("/subscriptions")
    public ResponseEntity<Void> unsubscribe(
            @AuthenticationPrincipal String memberId,
            @Valid @RequestBody UnsubscribePushRequest request
    ) {
        pushSubscriptionCommandUseCase.unsubscribe(
                MemberId.from(UUID.fromString(memberId)),
                request.getEndpoint()
        );
        return ResponseEntity.noContent().build();
    }
}
