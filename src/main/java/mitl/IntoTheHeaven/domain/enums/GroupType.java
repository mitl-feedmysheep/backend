package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupType {
    NORMAL("일반 소그룹"),
    NEWCOMER("새가족부");

    private final String description;
}
