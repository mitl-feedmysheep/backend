package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.ChurchResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupResponse;
import mitl.IntoTheHeaven.application.port.in.query.ChurchQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.GroupQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.MemberId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Church", description = "APIs for Church Management")
@RestController
@RequestMapping("/churches")
@RequiredArgsConstructor
public class ChurchController {

    private final ChurchQueryUseCase churchQueryUseCase;
    private final GroupQueryUseCase groupQueryUseCase;

    @Operation(summary = "Get My Churches", description = "Retrieves a list of churches the current user belongs to.")
    @GetMapping
    public ResponseEntity<List<ChurchResponse>> getMyChurches(@AuthenticationPrincipal String memberId) {
        List<Church> churches = churchQueryUseCase.getChurchesByMemberId(MemberId.from(UUID.fromString(memberId)));
        List<ChurchResponse> response = ChurchResponse.from(churches);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Groups in Church", description = "Retrieves a list of groups within a specific church that the current user belongs to.")
    @GetMapping("/{churchId}/groups")
    public ResponseEntity<List<GroupResponse>> getGroupsInChurch(
            @PathVariable UUID churchId,
            @AuthenticationPrincipal String memberId) {
        List<Group> groups = groupQueryUseCase.getGroupsByMemberIdAndChurchId(
                MemberId.from(UUID.fromString(memberId)), 
                ChurchId.from(churchId)
        );
        List<GroupResponse> response = GroupResponse.from(groups);
        return ResponseEntity.ok(response);
    }
}