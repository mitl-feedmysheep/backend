package mitl.IntoTheHeaven.adapter.out.email;

import mitl.IntoTheHeaven.application.port.out.EmailPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "mail.provider", havingValue = "gmail")
public class GmailEmailAdapter implements EmailPort {

    private final MailSender mailSender;
    private final String fromAddress;
    private final String replyToAddress;

    public GmailEmailAdapter(
            MailSender mailSender,
            @Value("${mail.from}") String fromAddress,
            @Value("${mail.reply-to:}") String replyToAddress
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.replyToAddress = replyToAddress;
    }

    @Override
    public void sendTextEmail(String toAddress, String subject, String bodyText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toAddress);
        message.setFrom(fromAddress);
        if (replyToAddress != null && !replyToAddress.isBlank()) {
            message.setReplyTo(replyToAddress);
        }
        message.setSubject(subject);
        message.setText(bodyText);
        mailSender.send(message);
    }
}


