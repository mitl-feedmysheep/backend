package mitl.IntoTheHeaven.adapter.in.web.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mitl.IntoTheHeaven.domain.enums.VerificationType;
import lombok.Getter;

@Getter
public class ConfirmEmailVerificationRequest {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code;

    @NotNull(message = "Verification type is required")
    private VerificationType type;
}


