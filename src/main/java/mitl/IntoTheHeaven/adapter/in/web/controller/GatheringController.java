package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.GatheringDetailResponse;
import mitl.IntoTheHeaven.application.port.in.query.GatheringQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Gathering", description = "APIs for Gathering Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/gatherings")
public class GatheringController {

    private final GatheringQueryUseCase gatheringQueryUseCase;

    @Operation(summary = "Get Gathering Details", description = "Retrieves detailed information of a specific gathering, including its members and prayers.")
    @GetMapping("/{gatheringId}")
    public ResponseEntity<GatheringDetailResponse> getGatheringDetail(
            @PathVariable UUID gatheringId
    ) {
        Gathering gathering = gatheringQueryUseCase.getGatheringDetail(GatheringId.from(gatheringId));
        GatheringDetailResponse response = GatheringDetailResponse.from(gathering);
        return ResponseEntity.ok(response);
    }
} 