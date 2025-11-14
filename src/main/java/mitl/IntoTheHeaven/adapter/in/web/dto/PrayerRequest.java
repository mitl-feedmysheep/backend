package mitl.IntoTheHeaven.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import mitl.IntoTheHeaven.application.port.in.command.dto.PrayerCommand;

public record PrayerRequest(
        @NotBlank(message = "Prayer request is required")
        String prayerRequest,

        String description
) {
    public static PrayerCommand toCommand(PrayerRequest request) {
        return PrayerCommand.builder()
                .prayerRequest(request.prayerRequest)
                .description(request.description)
                .build();
    }
}

