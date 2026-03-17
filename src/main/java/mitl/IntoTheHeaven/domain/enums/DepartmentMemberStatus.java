package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DepartmentMemberStatus {
    ACTIVE("활동 중"),
    GRADUATED("졸업");

    private final String description;
}
