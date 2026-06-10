package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReadingPlanDayRequest(
        @NotNull LocalDate readingDate,
        @Min(1) int dayNumber,
        @NotBlank String readingRange,
        String youtubeUrl,
        String description
) {}
