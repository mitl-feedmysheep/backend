package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.RequestStatus;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class ChurchMemberRequest extends DomainEntity<ChurchMemberRequest, ChurchMemberRequestId> {

    private final MemberId memberId;
    private final ChurchId churchId;
    private final DepartmentId departmentId;
    private final RequestStatus status;
    private final String churchName;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime deletedAt;
}
