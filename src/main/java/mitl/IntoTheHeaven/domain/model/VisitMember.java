package mitl.IntoTheHeaven.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
public class VisitMember extends DomainEntity<VisitMember, VisitMemberId> {

    private final VisitId visitId;
    private final ChurchMemberId churchMemberId;
    private final ChurchMember churchMember;
    private final String story;
    private final LocalDateTime createdAt;
    private final LocalDateTime deletedAt;

    @Builder.Default
    private final List<Prayer> prayers = new ArrayList<>();

    /**
     * Delete visit member (soft delete)
     */
    public VisitMember delete() {
        return this.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
    }
}

