package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.GatheringResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.MeResponse;
import mitl.IntoTheHeaven.application.port.in.query.GatheringQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.GroupQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.MemberQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Gathering;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Group", description = "APIs for Group Management")
@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupQueryUseCase groupQueryUseCase;
    private final GatheringQueryUseCase gatheringQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;

    @Operation(summary = "Get My Groups", description = "Retrieves a list of groups the current user belongs to.")
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups(@AuthenticationPrincipal String memberId) {
        List<Group> groups = groupQueryUseCase.getGroupsByMemberId(MemberId.from(UUID.fromString(memberId)));
        List<GroupResponse> response = GroupResponse.from(groups);  
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Gatherings in Group", description = "Retrieves a list of gatherings within a specific group.")
    @GetMapping("/{groupId}/gatherings")
    public ResponseEntity<List<GatheringResponse>> getGatheringsInGroup(@PathVariable UUID groupId) {
        List<Gathering> gatherings = gatheringQueryUseCase.getGatheringsByGroupId(GroupId.from(groupId));
        List<GatheringResponse> response = GatheringResponse.from(gatherings);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Members in Group", description = "Retrieves a list of members in a specific group.")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<MeResponse>> getMembersInGroup(@PathVariable UUID groupId) {
        List<Member> members = memberQueryUseCase.getMembersByGroupId(groupId);
        List<MeResponse> response = MeResponse.from(members);
        return ResponseEntity.ok(response);
    }
} 