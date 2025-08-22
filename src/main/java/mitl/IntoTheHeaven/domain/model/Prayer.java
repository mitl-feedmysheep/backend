package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class Prayer extends DomainEntity<Prayer, PrayerId> {

    private final Member member;
    private final MemberId memberId;
    private final GatheringMember gatheringMember;
    private final String prayerRequest;
    private final String description;
    private final boolean isAnswered;
    private final LocalDateTime createdAt;
} 