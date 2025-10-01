package mitl.IntoTheHeaven.adapter.in.web.dto;

import lombok.Builder;
import mitl.IntoTheHeaven.domain.model.Prayer;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PrayerResponse(
        UUID id,
        String prayerRequest,
        String description,
        boolean isAnswered,
        LocalDateTime createdAt
) {
    public static PrayerResponse from(Prayer prayer) {
        return PrayerResponse.builder()
                .id(prayer.getId().getValue())
                .prayerRequest(prayer.getPrayerRequest())
                .description(prayer.getDescription())
                .isAnswered(prayer.isAnswered())
                .createdAt(prayer.getCreatedAt())
                .build();
    }
}

