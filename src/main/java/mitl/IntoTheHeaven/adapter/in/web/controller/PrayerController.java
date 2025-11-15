package mitl.IntoTheHeaven.adapter.in.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.application.port.in.command.PrayerCommandUseCase;
import mitl.IntoTheHeaven.domain.model.PrayerId;

import java.util.UUID;

@Tag(name = "Prayer", description = "APIs for Prayer Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/prayers")
public class PrayerController {

    private final PrayerCommandUseCase prayerCommandUseCase;

    @Operation(summary = "Delete Prayer", description = "Deletes a prayer by ID (soft delete).")
    @DeleteMapping("/{prayerId}")
    public ResponseEntity<Void> deletePrayer(@PathVariable("prayerId") UUID prayerId) {
        prayerCommandUseCase.delete(PrayerId.from(prayerId));
        return ResponseEntity.ok().build();
    }
}
