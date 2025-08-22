package mitl.IntoTheHeaven.adapter.in.web.dto.prayer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrayerRequestCountByChurchResponse {
    private final int count;

    public static PrayerRequestCountByChurchResponse from(int count) {
        return PrayerRequestCountByChurchResponse.builder().count(count).build();
    }
}
