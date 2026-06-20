package mitl.IntoTheHeaven.adapter.in.web.dto.announcement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CreateAnnouncementRequest {

    @NotBlank
    private String entityType;

    @NotBlank
    private String entityId;

    @NotBlank
    private String title;

    @NotBlank
    private String body;

    @NotNull
    private LocalDateTime sendAt;

    private boolean pushEnabled = true;
}
