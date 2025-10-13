package mitl.IntoTheHeaven.adapter.in.web.dto.auth;

import mitl.IntoTheHeaven.domain.enums.VerificationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SendEmailVerificationRequest {
    @NotBlank
    @Email
    private String email;

    @NotNull(message = "Verification type is required")
    private VerificationType type;
}


