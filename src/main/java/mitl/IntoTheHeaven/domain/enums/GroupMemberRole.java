package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupMemberRole {
    LEADER("리더"),
    SUB_LEADER("서브 리더"),
    MEMBER("멤버");

    private final String description;
} 