package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {
    BIRTHDAY("BIRTHDAY"),
    NORMAL("NORMAL"),
    ADMIN_REQUEST("ADMIN_REQUEST");

    private final String value;
}
