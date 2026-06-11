package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReadingPlanRequest(
        @NotNull UUID churchId,
        @NotBlank String title,
        @Min(1) @Max(127) int readingDays
) {}
