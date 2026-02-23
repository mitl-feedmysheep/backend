package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.AdminMemberSearchResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.ChurchResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.AdminChurchResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.BirthdayMemberResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.JoinRequestResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupWithLeaderResponse;
import mitl.IntoTheHeaven.application.dto.GroupWithLeader;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.AdminSelectChurchRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.prayer.PrayerRequestCountByChurchResponse;
import mitl.IntoTheHeaven.application.dto.MemberWithGroups;
import mitl.IntoTheHeaven.global.aop.RequireChurchRole;
import mitl.IntoTheHeaven.global.security.JwtAuthenticationToken;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.ChurchMemberRequest;
import mitl.IntoTheHeaven.domain.model.Member;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import mitl.IntoTheHeaven.application.port.in.query.ChurchQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.command.AuthCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.ChurchCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.GroupQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.PrayerQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Church", description = "APIs for Church Management")
@RestController
@RequestMapping("/churches")
@RequiredArgsConstructor
public class ChurchController {

        private final ChurchQueryUseCase churchQueryUseCase;
        private final ChurchCommandUseCase churchCommandUseCase;
        private final AuthCommandUseCase authCommandUseCase;
        private final GroupQueryUseCase groupQueryUseCase;
        private final PrayerQueryUseCase prayerQueryUseCase;

        @Operation(summary = "Get My Churches", description = "Retrieves a list of churches the current user belongs to.")
        @GetMapping
        public ResponseEntity<List<ChurchResponse>> getMyChurches(@AuthenticationPrincipal String memberId) {
                List<Church> churches = churchQueryUseCase
                                .getChurchesByMemberId(MemberId.from(UUID.fromString(memberId)));
                List<ChurchResponse> response = ChurchResponse.from(churches);
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get Groups in Church", description = "Retrieves a list of groups within a specific church that the current user belongs to.")
        @GetMapping("/{churchId}/groups")
        public ResponseEntity<List<GroupResponse>> getGroupsInChurch(
                        @PathVariable("churchId") UUID churchId,
                        @AuthenticationPrincipal String memberId) {
                List<Group> groups = groupQueryUseCase.getGroupsByMemberIdAndChurchId(
                                MemberId.from(UUID.fromString(memberId)),
                                ChurchId.from(churchId));
                List<GroupResponse> response = groups.stream()
                                .map(g -> GroupResponse.from(g,
                                                groupQueryUseCase.getGroupMembersByGroupId(g.getId().getValue())
                                                                .size()))
                                .toList();
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get Prayer Requests in Church", description = "Retrieves a count of prayer requests within a specific church that the current user belongs to.")
        @GetMapping("/{churchId}/prayer-request-count")
        public ResponseEntity<PrayerRequestCountByChurchResponse> getPrayerRequestsInChurch(
                        @PathVariable("churchId") UUID churchId,
                        @AuthenticationPrincipal String memberId) {
                Long prayerRequestCount = prayerQueryUseCase.getPrayerRequestCountByMemberIdAndChurchId(
                                MemberId.from(UUID.fromString(memberId)),
                                ChurchId.from(churchId));

                PrayerRequestCountByChurchResponse response = PrayerRequestCountByChurchResponse
                                .from(prayerRequestCount);

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get Birthday Members", description = "Retrieves members whose birthday falls in the specified month within a church.")
        @GetMapping("/{churchId}/birthday-members")
        public ResponseEntity<List<BirthdayMemberResponse>> getBirthdayMembers(
                        @PathVariable("churchId") UUID churchId,
                        @RequestParam("month") int month) {
                List<Member> members = churchQueryUseCase.getBirthdayMembers(
                                ChurchId.from(churchId), month);
                List<BirthdayMemberResponse> response = members.stream()
                                .map(m -> BirthdayMemberResponse.builder()
                                                .memberId(m.getId().getValue())
                                                .name(m.getName())
                                                .birthday(m.getBirthday())
                                                .sex(m.getSex())
                                                .build())
                                .toList();
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get Groups with Leaders in Church", description = "Retrieves all groups and their leaders in a specific church. The caller must be a member of the church.")
        @GetMapping("/{churchId}/groups-with-leaders")
        public ResponseEntity<List<GroupWithLeaderResponse>> getGroupsWithLeaders(
                        @PathVariable("churchId") UUID churchId,
                        @AuthenticationPrincipal String memberId) {
                List<GroupWithLeader> groups = groupQueryUseCase.getGroupsWithLeaderByChurchId(ChurchId.from(churchId));
                return ResponseEntity.ok(GroupWithLeaderResponse.from(groups));
        }

        @Operation(summary = "Get All Churches", description = "Retrieves a list of all registered churches.")
        @GetMapping("/all")
        public ResponseEntity<List<ChurchResponse>> getAllChurches() {
                List<Church> churches = churchQueryUseCase.getAllChurches();
                return ResponseEntity.ok(ChurchResponse.from(churches));
        }

        @Operation(summary = "Create Join Request", description = "Creates a join request to a specific church for the current user.")
        @PostMapping("/{churchId}/join-request")
        public ResponseEntity<JoinRequestResponse> createJoinRequest(
                        @PathVariable("churchId") UUID churchId,
                        @AuthenticationPrincipal String memberId) {
                ChurchMemberRequest request = churchCommandUseCase.createJoinRequest(
                                MemberId.from(UUID.fromString(memberId)),
                                ChurchId.from(churchId));
                return ResponseEntity.ok(JoinRequestResponse.from(request));
        }

        @Operation(summary = "Get My Join Requests", description = "Retrieves the join request status for the current user.")
        @GetMapping("/join-request/status")
        public ResponseEntity<List<JoinRequestResponse>> getMyJoinRequests(
                        @AuthenticationPrincipal String memberId) {
                List<ChurchMemberRequest> requests = churchQueryUseCase
                                .getMyJoinRequests(MemberId.from(UUID.fromString(memberId)));
                return ResponseEntity.ok(JoinRequestResponse.from(requests));
        }

        /* ADMIN */
        @Operation(summary = "Get Admin Churches", description = "Retrieves churches where the current user is ADMIN.")
        @GetMapping("/admin")
        public ResponseEntity<List<AdminChurchResponse>> getAdminChurches(@AuthenticationPrincipal String memberId) {
                List<Church> churches = churchQueryUseCase.getAdminChurches(MemberId.from(UUID.fromString(memberId)));
                return ResponseEntity.ok(churches.stream()
                                .map(AdminChurchResponse::from)
                                .toList());
        }

        @Operation(summary = "Select Church (Issue context token)", description = "Issues a context token bound to the selected church.")
        @PostMapping("/admin/select-church")
        public ResponseEntity<LoginResponse> selectChurch(@AuthenticationPrincipal String memberId,
                        @Valid @RequestBody AdminSelectChurchRequest request) {
                LoginResponse response = authCommandUseCase.selectChurch(MemberId.from(UUID.fromString(memberId)),
                                ChurchId.from(request.getChurchId()));
                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Search Church Members", description = "Search members in a church by name or phone number.")
        @GetMapping("/admin/members")
        @RequireChurchRole(ChurchRole.LEADER)
        public ResponseEntity<List<AdminMemberSearchResponse>> searchChurchMembers(
                        @RequestParam("searchText") String searchText) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
                String churchId = jwtAuth.getChurchId();

                List<MemberWithGroups> members = churchQueryUseCase.searchChurchMembers(
                                ChurchId.from(UUID.fromString(churchId)), searchText);
                List<AdminMemberSearchResponse> response = AdminMemberSearchResponse.from(members);
                return ResponseEntity.ok(response);
        }
}