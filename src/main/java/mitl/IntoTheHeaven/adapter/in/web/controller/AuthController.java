package mitl.IntoTheHeaven.adapter.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.AvailabilityResponse;
import mitl.IntoTheHeaven.application.port.in.query.MemberQueryUseCase;
import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.LoginResponse;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.SignUpRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.member.SignUpResponse;
import mitl.IntoTheHeaven.application.port.in.command.LoginUseCase;
import mitl.IntoTheHeaven.application.port.in.command.MemberCommandUseCase;
import mitl.IntoTheHeaven.application.port.in.command.VerificationCommandUseCase;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.SendEmailVerificationRequest;
import mitl.IntoTheHeaven.adapter.in.web.dto.auth.ConfirmEmailVerificationRequest;
import mitl.IntoTheHeaven.domain.model.Member;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Tag(name = "Auth", description = "APIs for Authentication")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final MemberCommandUseCase memberCommandUseCase;
    private final MemberQueryUseCase memberQueryUseCase;
    private final VerificationCommandUseCase verificationCommandUseCase;

    @Operation(summary = "User Login", description = "Logs in a user with email and password.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse response = loginUseCase.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Send Email Verification Code", description = "Sends a verification code to the specified email.")
    @PostMapping("/verification/email")
    public ResponseEntity<Void> sendEmailVerification(@RequestBody @Valid SendEmailVerificationRequest request) {
        verificationCommandUseCase.sendEmailVerificationCode(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Confirm Email Verification Code", description = "Confirms the verification code for the specified email.")
    @PostMapping("/verification/email/confirm")
    public ResponseEntity<?> confirmEmailVerification(@RequestBody @Valid ConfirmEmailVerificationRequest request) {
        boolean ok = verificationCommandUseCase.confirmEmailVerificationCode(request.getEmail(), request.getCode());
        if (ok) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code");
    }


    @Operation(summary = "User Signup", description = "Registers a new user.")
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        Member newMember = memberCommandUseCase.signUp(signUpRequest.toCommand());
        SignUpResponse response = SignUpResponse.builder()
                .memberId(newMember.getId().getValue().toString())
                .message("User signed up successfully.")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Check Phone Availability", description = "Checks if a phone number is available for signup.")
    @org.springframework.web.bind.annotation.GetMapping("/availability/phone")
    public ResponseEntity<AvailabilityResponse> isPhoneAvailable(
            @Parameter(description = "Phone number to check", required = true)
            @org.springframework.web.bind.annotation.RequestParam("value")
            @NotBlank(message = "Phone number must not be blank")
            @Pattern(regexp = "^\\d{9,15}$", message = "Phone number must be 9-15 digits") String phone
    ) {
        boolean available = memberQueryUseCase.isPhoneAvailable(phone);
        return ResponseEntity.ok(AvailabilityResponse.of(available));
    }

    @Operation(summary = "Check Email Availability", description = "Checks if an email is available for signup.")
    @org.springframework.web.bind.annotation.GetMapping("/availability/email")
    public ResponseEntity<AvailabilityResponse> isEmailAvailable(
            @Parameter(description = "Email to check", required = true)
            @org.springframework.web.bind.annotation.RequestParam("value")
            @NotBlank(message = "Email must not be blank")
            @Email(message = "Invalid email format") String email
    ) {
        boolean available = memberQueryUseCase.isEmailAvailable(email);
        return ResponseEntity.ok(AvailabilityResponse.of(available));
    }
} 