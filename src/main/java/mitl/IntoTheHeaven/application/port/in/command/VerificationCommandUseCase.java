package mitl.IntoTheHeaven.application.port.in.command;

public interface VerificationCommandUseCase {

    void sendEmailVerificationCode(String email);

    boolean confirmEmailVerificationCode(String email, String code);
}


