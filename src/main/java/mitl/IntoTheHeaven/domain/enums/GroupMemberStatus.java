package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupMemberStatus {
    ACTIVE("활동 중"),
    GRADUATED("졸업"),
    REMOVED("제외");

    private final String description;
}
