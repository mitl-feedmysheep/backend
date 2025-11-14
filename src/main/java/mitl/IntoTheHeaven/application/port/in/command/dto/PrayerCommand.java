package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;

@Builder
public record PrayerCommand(
        String prayerRequest,
        String description
) {
}

