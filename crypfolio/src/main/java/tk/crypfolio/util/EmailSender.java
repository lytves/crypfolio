package tk.crypfolio.util;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class EmailSender {

    private static final Logger logger = Logger.getLogger(EmailSender.class.getName());

    private final static String FROM_EMAIL = "PUT_YOUR_GMAIL_ID_HERE";

    private final static String PASSWORD = "PUT_YOUR_GMAIL_PASSWORD_HERE";

    /*
     * Outgoing Mail (SMTP) Server
     * requires TLS or SSL: smtp.gmail.com (use authentication)
     * */

    public static void sendConfirmedEmail(String emailRecipient, String verificationCode) {

        String subject = "CrypFolio: confirm your email";

        String text = "<p>Thanks for registering an account on CrypFolio!</p>" +

                "<p>We need to confirm that you're a real human.</p>" +

                "<p>Follow the link to verify your email:</p>" +

                "<p><a href=\"https://crypfolio.tk/verify-email/confirm-link?code=" + verificationCode + "\">" +

                "https://crypfolio.tk/verify-email/confirm-link?code=" + verificationCode + "</a></p>";

        logger.log(Level.INFO, "Start sending sendConfirmedEmail()..");

        sendEmail(emailRecipient, subject, text);
    }

    private static void sendEmail(String emailRecipient, String subject, String text) {

        /*
        * thanks to App Shah
        * https://crunchify.com/java-mailapi-example-send-an-email-via-gmail-smtp/
        */

        // Step1
        logger.log(Level.INFO, "1st ===> setup Mail Server Properties..");

        Properties mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        logger.log(Level.INFO, "Mail Server Properties have been setup successfully..");


        // Step2
        logger.log(Level.INFO, "2nd ===> get Mail Session..");

        Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        MimeMessage generateMailMessage = new MimeMessage(getMailSession);
        try {

            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipient));
            generateMailMessage.setSubject(subject);
            generateMailMessage.setContent(text, "text/html");

        } catch (MessagingException e) {
            logger.log(Level.WARNING, e.getMessage());
        }

        logger.log(Level.INFO, "Mail Session has been created successfully..");


        // Step3
        logger.log(Level.INFO, "3rd ===> Get Session and Send mail");
        Transport transport = null;
        try {

            transport = getMailSession.getTransport("smtp");
            // Enter your correct gmail UserID and Password
            // if you have 2FA enabled then provide App Specific Password
            transport.connect("smtp.gmail.com", FROM_EMAIL, PASSWORD);
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());

            logger.log(Level.INFO, "Email was sent successfully!!");

        } catch (MessagingException e) {

            logger.log(Level.WARNING, e.getMessage());

        } finally {

            try {

                assert transport != null;
                transport.close();

            } catch (MessagingException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
    }
}