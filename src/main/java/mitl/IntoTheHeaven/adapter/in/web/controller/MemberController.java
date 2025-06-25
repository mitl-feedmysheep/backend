package mitl.IntoTheHeaven.adapter.in.web.controller;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.FindMemberResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.SignUpRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.SignUpResponse;
import mitl.IntoTheHeaven.application.port.in.command.MemberCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.query.MemberQueryUseCase;
import mitl.IntoTheHeaven.domain.model.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberQueryUseCase memberQueryUseCase;
    private final MemberCommandUseCase memberCommandUseCase;

    @GetMapping("/{memberId}")
    public ResponseEntity<FindMemberResponse> findMemberById(@PathVariable UUID memberId) {
        Member member = memberQueryUseCase.findMemberById(memberId);
        FindMemberResponse response = FindMemberResponse.fromDomain(member);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        Member newMember = memberCommandUseCase.signUp(signUpRequest.toCommand());
        SignUpResponse response = SignUpResponse.builder()
                .memberId(newMember.getId().toString())
                .message("회원가입이 성공적으로 완료되었습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 