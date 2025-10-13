package mitl.IntoTheHeaven.adapter.in.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VerifyMemberResponse {
    private final boolean exists;

    public static VerifyMemberResponse of(boolean exists) {
        return VerifyMemberResponse.builder()
                .exists(exists)
                .build();
    }
}
