package mitl.IntoTheHeaven.adapter.in.web.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SendEmailVerificationRequest {
    @NotBlank
    @Email
    private String email;
}


