package mitl.IntoTheHeaven.application.port.out;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class HomeSummaryData {
    private final UUID gatheringMemberId;
    private final String goal;
    private final LocalDate gatheringDate;
    private final String groupName;
}
