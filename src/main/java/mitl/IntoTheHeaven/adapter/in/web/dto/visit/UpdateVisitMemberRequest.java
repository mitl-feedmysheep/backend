package mitl.IntoTheHeaven.adapter.in.web.dto.visit;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateVisitMemberRequest {

    private String story;

    private List<PrayerRequest> prayers;

    @Getter
    @Setter
    public static class PrayerRequest {
        private UUID id; // optional: existing prayer ID for update
        private String prayerRequest;
        private String description;
    }
}


