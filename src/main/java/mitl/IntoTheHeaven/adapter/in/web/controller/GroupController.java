package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.gathering.GatheringResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.GroupMemberResponse;
import mitl.IntoTheHeaven.application.port.in.query.GatheringQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.GetMyGroupMemberInfoUseCase;
import mitl.IntoTheHeaven.application.port.in.query.GroupQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.GroupId;
import mitl.IntoTheHeaven.domain.model.GroupMember;
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
    private final GetMyGroupMemberInfoUseCase getMyGroupMemberInfoUseCase;

    @Operation(summary = "Get All My Groups", description = "Retrieves a list of groups the current user belongs to.")
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups(@AuthenticationPrincipal String memberId) {
        List<Group> groups = groupQueryUseCase.getGroupsByMemberId(MemberId.from(UUID.fromString(memberId)));
        List<GroupResponse> response = GroupResponse.from(groups);  
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Gatherings in Group", description = "Retrieves a list of gatherings within a specific group.")
    @GetMapping("/{groupId}/gatherings")
    public ResponseEntity<List<GatheringResponse>> getGatheringsInGroup(@PathVariable UUID groupId) {
        List<GatheringResponse> response = gatheringQueryUseCase.getGatheringsWithStatisticsByGroupId(GroupId.from(groupId))
                .stream()
                .map(gws -> GatheringResponse.from(
                    gws.getGathering(),
                    gws.getNth(),
                    gws.getTotalWorshipAttendanceCount(),
                    gws.getTotalGatheringAttendanceCount(),
                    gws.getTotalPrayerRequestCount()
                ))
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get Members in Group", description = "Retrieves a list of members with roles in a specific group.")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMemberResponse>> getMembersInGroup(@PathVariable UUID groupId) {
        List<GroupMember> groupMembers = groupQueryUseCase.getGroupMembersByGroupId(groupId);
        List<GroupMemberResponse> response = GroupMemberResponse.from(groupMembers);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get My Info in Group", description = "Retrieves my information and role in a specific group.")
    @GetMapping("/{groupId}/me")
    public ResponseEntity<GroupMemberResponse> getMyInfoInGroup(
            @PathVariable UUID groupId,
            @AuthenticationPrincipal String memberId) {
        GroupMember groupMember = getMyGroupMemberInfoUseCase.getMyGroupMemberInfo(
                GroupId.from(groupId), 
                MemberId.from(UUID.fromString(memberId))
        );
        GroupMemberResponse response = GroupMemberResponse.from(groupMember);
        return ResponseEntity.ok(response);
    }
} 