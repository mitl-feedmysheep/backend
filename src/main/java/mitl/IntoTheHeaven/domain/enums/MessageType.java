package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    BIRTHDAY("BIRTHDAY"),
    NORMAL("NORMAL");

    private final String value;
}
