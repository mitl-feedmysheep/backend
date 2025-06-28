package mitl.IntoTheHeaven.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.GatheringDetailResponse;
import mitl.IntoTheHeaven.application.port.in.query.GatheringQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.GatheringId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/gatherings")
public class GatheringController {

    private final GatheringQueryUseCase gatheringQueryUseCase;

    @GetMapping("/{gatheringId}")
    public ResponseEntity<GatheringDetailResponse> getGatheringDetail(
            @PathVariable UUID gatheringId
    ) {
        Gathering gathering = gatheringQueryUseCase.getGatheringDetail(GatheringId.from(gatheringId));
        GatheringDetailResponse response = GatheringDetailResponse.from(gathering);
        return ResponseEntity.ok(response);
    }
} 