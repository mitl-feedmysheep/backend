package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

@Getter
@SuperBuilder
public class Prayer extends DomainEntity<Prayer, PrayerId> {

    private final Member member;
    private final GatheringMember gatheringMember;
    private final String prayerRequest;
    private final String description;
    private final boolean isAnswered;
} 