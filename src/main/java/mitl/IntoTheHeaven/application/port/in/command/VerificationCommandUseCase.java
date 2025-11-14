package mitl.IntoTheHeaven.application.port.in.command;

import mitl.IntoTheHeaven.domain.enums.VerificationType;

public interface VerificationCommandUseCase {

    void sendEmailVerificationCode(String email, VerificationType type);

    boolean confirmEmailVerificationCode(String email, String code, VerificationType type);
}
