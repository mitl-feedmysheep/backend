package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import lombok.Builder;
import lombok.Data;
import mitl.IntoTheHeaven.domain.model.Prayer;

import java.util.UUID;

@Data
@Builder
public class PrayerResponse {

    private UUID id;
    private String prayerRequest;
    private String description;
    private boolean isAnswered;

    public static PrayerResponse from(Prayer prayer) {
        return PrayerResponse.builder()
                .id(prayer.getId().getValue())
                .prayerRequest(prayer.getPrayerRequest())
                .description(prayer.getDescription())
                .isAnswered(prayer.isAnswered())
                .build();
    }
} 