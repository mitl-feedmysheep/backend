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
    public void sendEmailVerificationCode(String email) {
        String code = generateRandomCode(CODE_LENGTH);

        VerificationJpaEntity entity = VerificationJpaEntity.builder()
                .id(UUID.randomUUID())
                .type(VerificationType.EMAIL)
                .typeValue(email.toLowerCase(Locale.ROOT))
                .code(code)
                .build();

        verificationRepository.save(entity);

        String subject = "Your verification code";
        String body = "Your verification code is: " + code + "\nIt expires in 10 minutes.";
        emailPort.sendTextEmail(email, subject, body);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean confirmEmailVerificationCode(String email, String code) {
        // Find the latest verification entity for the given email
        VerificationJpaEntity entity = verificationRepository
                .findTopByTypeAndTypeValueOrderByCreatedAtDesc(VerificationType.EMAIL, email.toLowerCase(Locale.ROOT))
                .orElse(null);

        if (entity == null) {
            return false;
        }

        // Check if code matches
        if (!entity.getCode().equals(code)) {
            return false;
        }

        // Check if createdAt is within 1 hour
        // Assume getCreatedAt() returns a java.time.LocalDateTime
        java.time.LocalDateTime createdAt = entity.getCreatedAt();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(createdAt, now);

        // 1 hour = 60 minutes
        return duration.toMinutes() <= 60;
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


