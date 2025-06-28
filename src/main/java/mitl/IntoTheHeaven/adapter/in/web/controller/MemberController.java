package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.MeResponse;
import mitl.IntoTheHeaven.application.port.in.query.MemberQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Tag(name = "Member", description = "APIs for Member Management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberQueryUseCase memberQueryUseCase;

    @Operation(summary = "Get My Info", description = "Retrieves the information of the currently logged-in user.")
    @GetMapping("/me")
    public ResponseEntity<MeResponse> getMe(@AuthenticationPrincipal String memberId) {
        Member member = memberQueryUseCase.getMemberById(MemberId.from(UUID.fromString(memberId)));
        MeResponse response = MeResponse.from(member);
        return ResponseEntity.ok(response);
    }
} 