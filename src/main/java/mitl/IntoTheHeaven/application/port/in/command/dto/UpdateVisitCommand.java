package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UpdateVisitCommand(
                LocalDate date,
                LocalDateTime startedAt,
                LocalDateTime endedAt,
                String place,
                Integer expense,
                String notes) {
}
