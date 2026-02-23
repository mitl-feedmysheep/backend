package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePrayerAnsweredRequest(
        @NotNull(message = "isAnswered is required")
        Boolean isAnswered
) {}
