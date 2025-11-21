package mitl.IntoTheHeaven.application.port.in.query.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.enums.ChurchRole;

@Getter
@Builder
public class AdminMeResponse {
    private final ChurchRole role;

    public static AdminMeResponse from(ChurchRole role) {
        return AdminMeResponse.builder()
                .role(role)
                .build();
    }
}

