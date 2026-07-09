package mitl.IntoTheHeaven.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    ADMIN_COMMENT("ADMIN_COMMENT"),
    GATHERING_USER_CARD_UPDATED("GATHERING_USER_CARD_UPDATED"),
    JOIN_REQUEST("JOIN_REQUEST"),
    REPORT_CREATED("REPORT_CREATED"),
    REPORT_COMMENT_ADDED("REPORT_COMMENT_ADDED");

    private final String value;
}
