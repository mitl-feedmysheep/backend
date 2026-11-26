package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PushDeliveryCommandUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * 푸시 클릭 수집. 서비스워커는 인증 토큰에 접근할 수 없으므로 인증 없이 허용한다
 * (deliveryId는 추측 불가능한 UUID이고, 최초 클릭만 반영되므로 남용 영향이 작다).
 */
@Tag(name = "Push Delivery")
@RestController
@RequiredArgsConstructor
@RequestMapping("/push/deliveries")
public class PushDeliveryController {

    private final PushDeliveryCommandUseCase pushDeliveryCommandUseCase;

    @Operation(summary = "Record push notification click")
    @PostMapping("/{deliveryId}/click")
    public ResponseEntity<Void> recordClick(@PathVariable String deliveryId) {
        try {
            pushDeliveryCommandUseCase.recordClick(UUID.fromString(deliveryId));
        } catch (IllegalArgumentException ignored) {
            // 잘못된 ID는 조용히 무시
        }
        return ResponseEntity.noContent().build();
    }
}
