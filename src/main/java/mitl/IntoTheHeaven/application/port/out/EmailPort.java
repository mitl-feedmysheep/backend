package mitl.IntoTheHeaven.application.port.out;

/**
 * Outbound port for sending emails.
 */
public interface EmailPort {

    /**
     * Sends a simple text email.
     *
     * @param toAddress recipient email address
     * @param subject   email subject
     * @param bodyText  plain text body
     */
    void sendTextEmail(String toAddress, String subject, String bodyText);
}


