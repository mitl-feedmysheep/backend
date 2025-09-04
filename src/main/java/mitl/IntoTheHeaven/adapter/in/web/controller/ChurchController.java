package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.ChurchResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.AdminChurchResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.group.GroupResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.church.AdminSelectChurchRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.prayer.PrayerRequestCountByChurchResponse;
import mitl.IntoTheHeaven.application.port.in.query.ChurchQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.command.AuthCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.GroupQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.PrayerQueryUseCase;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;
import mitl.IntoTheHeaven.domain.model.Church;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Group;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.global.aop.RequireChurchRole;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
                        @PathVariable UUID churchId,
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
                        @PathVariable UUID churchId,
                        @AuthenticationPrincipal String memberId) {
                Long prayerRequestCount = prayerQueryUseCase.getPrayerRequestCountByMemberIdAndChurchId(
                                MemberId.from(UUID.fromString(memberId)),
                                ChurchId.from(churchId));

                PrayerRequestCountByChurchResponse response = PrayerRequestCountByChurchResponse
                                .from(prayerRequestCount);

                return ResponseEntity.ok(response);
        }

        /* ADMIN */
    @Operation(summary = "Get Admin Churches", description = "Retrieves churches where the current user is ADMIN.")
    @RequireChurchRole(ChurchRole.ADMIN)
    @GetMapping("/admin")
    public ResponseEntity<List<AdminChurchResponse>> getAdminChurches(@AuthenticationPrincipal String memberId) {
        List<Church> churches = churchQueryUseCase.getAdminChurches(MemberId.from(UUID.fromString(memberId)));
        return ResponseEntity.ok(churches.stream()
                .map(AdminChurchResponse::from)
                .toList());
    }

    @Operation(summary = "Select Church (Issue context token)", description = "Issues a context token bound to the selected church.")
    @RequireChurchRole(ChurchRole.ADMIN)
    @PostMapping("/admin/select-church")
    public ResponseEntity<LoginResponse> selectChurch(@AuthenticationPrincipal String memberId,
            @Valid @RequestBody AdminSelectChurchRequest request) {
        LoginResponse response = authCommandUseCase.selectChurch(MemberId.from(UUID.fromString(memberId)),
                ChurchId.from(request.getChurchId()));
        return ResponseEntity.ok(response);
    }
}