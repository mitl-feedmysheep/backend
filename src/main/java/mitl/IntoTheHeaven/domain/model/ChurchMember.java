package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class ChurchMember extends DomainEntity<ChurchMember, ChurchMemberId> {

    private final ChurchId churchId;
    private final Member member;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;
}