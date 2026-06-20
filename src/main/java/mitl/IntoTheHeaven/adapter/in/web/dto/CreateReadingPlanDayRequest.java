package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateReadingPlanDayRequest(
        @Min(1) int dayNumber,
        @NotBlank String readingRange,
        String audioUrl,
        String videoUrl,
        String description
) {}
