package mitl.IntoTheHeaven.adapter.in.web.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Boolean isProvisioned;
} 