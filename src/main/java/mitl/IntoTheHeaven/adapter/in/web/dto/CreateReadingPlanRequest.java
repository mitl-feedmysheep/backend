package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReadingPlanRequest(
        @NotBlank String title,
        @NotNull LocalDate startDate,
        @Min(1) int totalDays
) {}
