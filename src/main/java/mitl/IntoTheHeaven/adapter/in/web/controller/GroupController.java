package mitl.IntoTheHeaven.adapter.in.web.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupQueryUseCase groupQueryUseCase;
    private final GatheringQueryUseCase gatheringQueryUseCase;
    private final MemberQueryUseCase memberQueryUseCase;

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups(@AuthenticationPrincipal String memberId) {
        List<Group> groups = groupQueryUseCase.getGroupsByMemberId(UUID.fromString(memberId));
        List<GroupResponse> response = GroupResponse.from(groups);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}/gatherings")
    public ResponseEntity<List<GatheringResponse>> getGatheringsInGroup(@PathVariable UUID groupId) {
        List<Gathering> gatherings = gatheringQueryUseCase.getGatheringsByGroupId(new GroupId(groupId));
        List<GatheringResponse> response = GatheringResponse.from(gatherings);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<MeResponse>> getMembersInGroup(@PathVariable UUID groupId) {
        List<Member> members = memberQueryUseCase.getMembersByGroupId(groupId);
        List<MeResponse> response = MeResponse.from(members);
        return ResponseEntity.ok(response);
    }
} 