package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    ADMIN_COMMENT("ADMIN_COMMENT"),
    GATHERING_USER_CARD_UPDATED("GATHERING_USER_CARD_UPDATED"),
    JOIN_REQUEST("JOIN_REQUEST");

    private final String value;
}
