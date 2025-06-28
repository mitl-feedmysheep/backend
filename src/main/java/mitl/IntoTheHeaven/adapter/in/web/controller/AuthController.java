package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.SignUpRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.SignUpResponse;
import mitl.IntoTheHeaven.application.port.in.command.LoginUseCase;
import mitl.IntoTheHeaven.application.port.in.command.MemberCommandUseCase;
import mitl.IntoTheHeaven.domain.model.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "APIs for Authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final MemberCommandUseCase memberCommandUseCase;

    @Operation(summary = "User Login", description = "Logs in a user with email and password.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = loginUseCase.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User Signup", description = "Registers a new user.")
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        Member newMember = memberCommandUseCase.signUp(signUpRequest.toCommand());
        SignUpResponse response = SignUpResponse.builder()
                .memberId(newMember.getId().getValue().toString())
                .message("User signed up successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 