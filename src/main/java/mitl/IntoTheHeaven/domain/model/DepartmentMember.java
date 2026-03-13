package mitl.IntoTheHeaven.domain.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import mitl.IntoTheHeaven.domain.enums.DepartmentMemberStatus;
import mitl.IntoTheHeaven.domain.enums.DepartmentRole;
import mitl.IntoTheHeaven.global.domain.DomainEntity;

@Getter
@SuperBuilder
public class DepartmentMember extends DomainEntity<DepartmentMember, DepartmentMemberId> {

    private final DepartmentId departmentId;
    private final Member member;
    private final DepartmentRole role;
    private final DepartmentMemberStatus status;
}
