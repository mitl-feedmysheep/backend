package mitl.IntoTheHeaven.application.port.in.command.dto;

import lombok.Builder;
import lombok.Getter;
import mitl.IntoTheHeaven.domain.model.MemberId;

@Getter
@Builder
public class SubscribePushCommand {

    private final MemberId memberId;
    private final String endpoint;
    private final String p256dh;
    private final String auth;
    private final String userAgent;
    private final String timezone;
}
