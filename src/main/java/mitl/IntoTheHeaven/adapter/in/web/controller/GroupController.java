package mitl.IntoTheHeaven.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupResponse;
import mitl.IntoTheHeaven.application.port.in.query.GroupQueryUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupQueryUseCase groupQueryUseCase;

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups(@AuthenticationPrincipal String memberId) {
        List<GroupResponse> response = groupQueryUseCase.getGroupsByMemberId(UUID.fromString(memberId));
        return ResponseEntity.ok(response);
    }
} 