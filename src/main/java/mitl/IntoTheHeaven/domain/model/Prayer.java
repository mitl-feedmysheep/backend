package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@SuperBuilder(toBuilder = true)
public class Prayer extends DomainEntity<Prayer, PrayerId> {

    private final Member member;
    private final MemberId memberId;
    private final GatheringMemberId gatheringMemberId;
    private final GatheringMember gatheringMember;
    private final VisitMemberId visitMemberId;
    private final VisitMember visitMember;
    private final String prayerRequest;
    private final String description;
    private final boolean isAnswered;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    private final String groupName;
    private final LocalDate gatheringDate;

    public Prayer delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }

    public Prayer markAnswered(boolean answered) {
        return this.toBuilder()
                .isAnswered(answered)
                .build();
    }
} 