package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

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

    public Prayer delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }
} 