package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.ChangeEmailRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.ChangePasswordRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.MeResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.UpdateMyProfileRequest;
import mitl.IntoTheHeaven.application.port.in.command.MemberCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.MemberQueryUseCase;
import mitl.IntoTheHeaven.application.port.in.query.dto.AdminMeResponse;
import mitl.IntoTheHeaven.domain.model.ChurchId;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;
import mitl.IntoTheHeaven.global.security.JwtAuthenticationToken;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

@Tag(name = "Member", description = "APIs for Member Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberQueryUseCase memberQueryUseCase;
    private final MemberCommandUseCase memberCommandUseCase;

    @Operation(summary = "Get My Info", description = "Retrieves the information of the currently logged-in user.")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> getMe(@AuthenticationPrincipal String memberId) {
        Member member = memberQueryUseCase.getMemberById(MemberId.from(UUID.fromString(memberId)));
        MeResponse response = MeResponse.from(member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Change Password", description = "Changes the password for the authenticated user.")
    @PostMapping("/password/change")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal String memberId,
            @RequestBody @Valid ChangePasswordRequest request) {
        Boolean result = memberCommandUseCase.changePassword(MemberId.from(UUID.fromString(memberId)),
                request.getCurrentPassword(), request.getNewPassword());
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change Email", description = "Changes the email for the authenticated user.")
    @PostMapping("/email/change")
    public ResponseEntity<Void> changeEmail(@AuthenticationPrincipal String memberId,
            @RequestBody @Valid ChangeEmailRequest request) {
        boolean result = memberCommandUseCase.changeEmail(MemberId.from(UUID.fromString(memberId)),
                request.getNewEmail());
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update My Profile", description = "Updates name, sex, birthday, and phone of the authenticated user. Fails if request.id differs from token subject.")
    @PatchMapping("/me")
    public ResponseEntity<MeResponse> updateMyProfile(
            @AuthenticationPrincipal String principalMemberId,
            @RequestBody @Valid UpdateMyProfileRequest request) {
        if (request.getId() == null || !principalMemberId.equals(request.getId())) {
            return ResponseEntity.status(403).build();
        }

        Member updated = memberCommandUseCase.updateMyProfile(request.toCommand());
        return ResponseEntity.ok(MeResponse.from(updated));
    }

    @Operation(summary = "Get My Admin Info in Church", description = "Retrieves my information including role in the current church context. Intended for admin context usage.")
    @GetMapping("/admin/me")
    public ResponseEntity<AdminMeResponse> getAdminMe(
            @AuthenticationPrincipal String memberId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) auth;
        String churchId = jwtAuth.getChurchId();

        AdminMeResponse response = memberQueryUseCase.getAdminMyInfo(
                MemberId.from(UUID.fromString(memberId)),
                ChurchId.from(UUID.fromString(churchId)));
        return ResponseEntity.ok(response);
    }

}