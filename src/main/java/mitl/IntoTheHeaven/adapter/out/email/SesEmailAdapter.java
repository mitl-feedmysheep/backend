package mitl.IntoTheHeaven.adapter.out.email;

import mitl.IntoTheHeaven.application.port.out.EmailPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

/**
 * SES-based implementation of EmailPort.
 */
@Component
public class SesEmailAdapter implements EmailPort {

    private final SesV2Client sesClient;
    private final String fromAddress;
    private final String replyToAddress;

    public SesEmailAdapter(
            SesV2Client sesClient,
            @Value("${mail.from}") String fromAddress,
            @Value("${mail.reply-to:}") String replyToAddress
    ) {
        this.sesClient = sesClient;
        this.fromAddress = fromAddress;
        this.replyToAddress = replyToAddress;
    }

    @Override
    public void sendTextEmail(String toAddress, String subject, String bodyText) {
        Destination destination = Destination.builder()
                .toAddresses(toAddress)
                .build();

        Message message = Message.builder()
                .subject(Content.builder().data(subject).charset("UTF-8").build())
                .body(Body.builder()
                        .text(Content.builder().data(bodyText).charset("UTF-8").build())
                        .build())
                .build();

        EmailContent emailContent = EmailContent.builder()
                .simple(message)
                .build();

        SendEmailRequest.Builder requestBuilder = SendEmailRequest.builder()
                .fromEmailAddress(fromAddress)
                .destination(destination)
                .content(emailContent);

        if (replyToAddress != null && !replyToAddress.isBlank()) {
            requestBuilder = requestBuilder.replyToAddresses(replyToAddress);
        }

        sesClient.sendEmail(requestBuilder.build());
    }
}



