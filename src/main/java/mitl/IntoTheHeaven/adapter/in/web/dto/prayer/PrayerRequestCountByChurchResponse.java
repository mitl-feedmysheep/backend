package mitl.IntoTheHeaven.adapter.in.web.dto.prayer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrayerRequestCountByChurchResponse {
    private final long count;

    public static PrayerRequestCountByChurchResponse from(Long count) {
        return PrayerRequestCountByChurchResponse.builder().count(count).build();
    }
}
