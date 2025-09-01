package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;
import mitl.IntoTheHeaven.domain.model.Member;
import mitl.IntoTheHeaven.domain.model.MemberId;

public interface MemberCommandUseCase {
    Member signUp(SignUpCommand command);

    Boolean changePassword(MemberId memberId, String currentPassword, String newPassword);

    Boolean changeEmail(MemberId memberId, String newEmail);
} 