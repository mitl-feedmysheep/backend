package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.application.port.in.command.dto.SignUpCommand;
import mitl.IntoTheHeaven.domain.model.Member;

public interface MemberCommandUseCase {
    Member signUp(SignUpCommand command);
} 