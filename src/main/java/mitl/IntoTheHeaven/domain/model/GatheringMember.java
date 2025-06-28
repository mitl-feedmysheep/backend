package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.util.List;

@Getter
@SuperBuilder
public class GatheringMember extends DomainEntity<GatheringMember, GatheringMemberId> {

    private final Member member;
    private final String name;
    private final boolean worshipAttendance;
    private final boolean gatheringAttendance;
    private final String story;
    private final List<Prayer> prayers;
} 