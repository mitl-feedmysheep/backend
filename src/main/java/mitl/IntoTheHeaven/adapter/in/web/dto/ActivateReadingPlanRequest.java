package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record ActivateReadingPlanRequest(
        @NotNull UUID readingPlanId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}
