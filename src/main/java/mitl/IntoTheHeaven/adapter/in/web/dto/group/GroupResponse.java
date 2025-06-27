package mitl.IntoTheHeaven.adapter.in.web.dto.group;

import java.time.LocalDate;
import java.util.UUID;

public record GroupResponse(
        UUID id,
        String name,
        String description,
        UUID churchId,
        LocalDate startDate,
        LocalDate endDate
) {
} 