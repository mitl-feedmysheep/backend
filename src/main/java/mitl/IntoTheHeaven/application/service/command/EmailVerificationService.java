package mitl.IntoTheHeaven.application.service.command;

import lombok.RequiredArgsConstructor;
import mitl.IntoTheHeaven.adapter.out.persistence.entity.VerificationJpaEntity;
import mitl.IntoTheHeaven.adapter.out.persistence.repository.VerificationJpaRepository;
import mitl.IntoTheHeaven.application.port.in.command.VerificationCommandUseCase;
import mitl.IntoTheHeaven.application.port.out.EmailPort;
import mitl.IntoTheHeaven.domain.enums.VerificationType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailVerificationService implements VerificationCommandUseCase {

    private final EmailPort emailPort;
    private final VerificationJpaRepository verificationRepository;

    private static final String CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 10;

    @Override
    @Transactional
    public void sendEmailVerificationCode(String email, VerificationType type) {
        String code = generateRandomCode(CODE_LENGTH);

        VerificationJpaEntity entity = VerificationJpaEntity.builder()
                .id(UUID.randomUUID())
                .type(type)
                .typeValue(email.toLowerCase(Locale.ROOT))
                .code(code)
                .build();

        verificationRepository.save(entity);

        String subject = getEmailSubject(type);
        String body = getEmailBody(type, code);
        emailPort.sendTextEmail(email, subject, body);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean confirmEmailVerificationCode(String email, String code, VerificationType type) {
        // Find the latest verification entity for the given email
        VerificationJpaEntity entity = verificationRepository
                .findTopByTypeAndTypeValueOrderByCreatedAtDesc(type, email.toLowerCase(Locale.ROOT))
                .orElse(null);

        if (entity == null) {
            return false;
        }

        // Check if code matches
        if (!entity.getCode().equals(code)) {
            return false;
        }

        // Check if createdAt is within 10 minutes
        // Assume getCreatedAt() returns a java.time.LocalDateTime
        java.time.LocalDateTime createdAt = entity.getCreatedAt();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(createdAt, now);

        // 10 minutes
        return duration.toMinutes() <= 10;
    }

    private String getEmailSubject(VerificationType type) {
        return switch (type) {
            case SIGNUP -> "Your verification code";
            case PASSWORD_RESET -> "Password Reset Verification Code";
            default -> "Verification Code";
        };
    }

    private String getEmailBody(VerificationType type, String code) {
        String message = switch (type) {
            case SIGNUP -> "Your verification code is: " + code;
            case PASSWORD_RESET -> "Your password reset verification code is: " + code;
            default -> "Your verification code is: " + code;
        };
        return message + "\nIt expires in 10 minutes.";
    }

    private String generateRandomCode(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = random.nextInt(CODE_ALPHABET.length());
            builder.append(CODE_ALPHABET.charAt(idx));
        }
        return builder.toString();
    }
}
