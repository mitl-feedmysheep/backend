package mitl.IntoTheHeaven.adapter.in.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AvailabilityResponse {
    private final boolean available;

    public static AvailabilityResponse of(boolean available) {
        return AvailabilityResponse.builder().available(available).build();
    }
}


