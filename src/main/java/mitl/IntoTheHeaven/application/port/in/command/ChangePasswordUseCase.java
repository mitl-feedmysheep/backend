package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.model.MemberId;

public interface ChangePasswordUseCase {
    Boolean changePassword(MemberId memberId, String currentPassword, String newPassword);
}


