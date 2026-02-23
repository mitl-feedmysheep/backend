package mitl.IntoTheHeaven.adapter.in.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.MyPrayerResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.UpdatePrayerAnsweredRequest;
import mitl.IntoTheHeaven.application.port.in.command.PrayerCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.PrayerQueryUseCase;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.domain.model.Prayer;
import mitl.IntoTheHeaven.domain.model.PrayerId;

import java.util.List;
import java.util.UUID;

@Tag(name = "Prayer", description = "APIs for Prayer Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/prayers")
public class PrayerController {

    private final PrayerCommandUseCase prayerCommandUseCase;
    private final PrayerQueryUseCase prayerQueryUseCase;

    @Operation(summary = "Get My Prayers", description = "Retrieves all prayers belonging to the current user, ordered by creation date descending.")
    @GetMapping("/me")
    public ResponseEntity<List<MyPrayerResponse>> getMyPrayers(@AuthenticationPrincipal String memberId) {
        List<Prayer> prayers = prayerQueryUseCase.getMyPrayers(MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok(MyPrayerResponse.from(prayers));
    }

    @Operation(summary = "Update Prayer Answered Status", description = "Toggles the answered status of a prayer.")
    @PatchMapping("/{prayerId}/answered")
    public ResponseEntity<Void> updatePrayerAnswered(
            @PathVariable("prayerId") UUID prayerId,
            @RequestBody @Valid UpdatePrayerAnsweredRequest request) {
        prayerCommandUseCase.updateAnswered(PrayerId.from(prayerId), request.isAnswered());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete Prayer", description = "Deletes a prayer by ID (soft delete).")
    @DeleteMapping("/{prayerId}")
    public ResponseEntity<Void> deletePrayer(@PathVariable("prayerId") UUID prayerId) {
        prayerCommandUseCase.delete(PrayerId.from(prayerId));
        return ResponseEntity.ok().build();
    }
}
