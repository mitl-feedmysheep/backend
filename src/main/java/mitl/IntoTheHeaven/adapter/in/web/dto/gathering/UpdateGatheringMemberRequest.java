package mitl.IntoTheHeaven.adapter.in.web.dto.gathering;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateGatheringMemberRequest {

    @NotNull(message = "Worship attendance status is required")
    private Boolean worshipAttendance;

    @NotNull(message = "Gathering attendance status is required")
    private Boolean gatheringAttendance;

    private String story;

    private List<PrayerRequest> prayers;

    @Getter
    @Setter
    public static class PrayerRequest {
        private String prayerRequest;
        private String description;
    }
} 