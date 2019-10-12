package tk.crypfolio.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.common.Settings;

import javax.ejb.Asynchronous;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public abstract class EmailSender {

    private static final Logger LOGGER = LogManager.getLogger(EmailSender.class);

    /*
     * Outgoing Mail (SMTP) Server
     * requires TLS or SSL: smtp.gmail.com (use authentication)
     * */

    public static void sendConfirmationEmail(String emailRecipient, String verificationCode) {

        String subject = "CrypFolio: confirm your email";

        String text = "<p>Thanks for registering an account on CrypFolio!</p>" +

                "<p>We need to confirm that you're a real human.</p>" +

                "<p>Follow the link to verify your email:</p>" +

                "<p><a href=\"http://crypfolio.tk/verify-email/confirm-link?code=" + verificationCode + "\">" +

                "http://crypfolio.tk/verify-email/confirm-link?code=" + verificationCode + "</a></p>" +

                "<p></p><p>-----------------</p>" +

                "<p>If you have been already confirmed your email, just ignore this email.</p>" +

                "<p>No one is able to hack your CrypFolio account, </p>" +

                "<p>unless they have also compromised your email address.</p>" +

                "<p></p><p></p>" +

                "<p>For security reasons, this link will expire in 30 minutes.</p>";

        LOGGER.info("Start sending sendConfirmationEmail()..");

        sendEmail(emailRecipient, subject, text);
    }

    public static void sendResetPasswordEmail(String emailRecipient, String resetCode) {

        String subject = "CrypFolio: reset password";

        String text = "<p>Follow this link and set your new CrypFolio account password:</p>" +

                "<p><a href=\"http://crypfolio.tk/reset-password/reset-link?code=" + resetCode + "\">" +

                "http://crypfolio.tk/reset-password/reset-link?code=" + resetCode + "</a></p>" +

                "<p></p><p>-----------------</p>" +

                "<p>If you did not request to reset your password, just ignore this email.</p>" +

                "<p>No one is able to reset your CrypFolio password without your permission, </p>" +

                "<p>unless they have also compromised your email address.</p>" +

                "<p></p><p></p>" +

                "<p>For security reasons, this link will expire in 30 minutes.</p>";

        LOGGER.info("Start sending sendResetPasswordEmail()..");

        sendEmail(emailRecipient, subject, text);
    }

    @Asynchronous
    private static void sendEmail(String emailRecipient, String subject, String text) {

        /*
        * thanks to App Shah
        * https://crunchify.com/java-mailapi-example-send-an-email-via-gmail-smtp/
        */

        // Step1
        LOGGER.info("1st ===> setup Mail Server Properties..");

        Properties mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");

        LOGGER.info( "Mail Server Properties have been setup successfully..");

        // Step2
        LOGGER.info("2nd ===> get Mail Session..");

        Session getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        MimeMessage generateMailMessage = new MimeMessage(getMailSession);
        try {

            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(emailRecipient));
            generateMailMessage.setSubject(subject);
            generateMailMessage.setContent(text, "text/html");

        } catch (MessagingException ex) {
            LOGGER.warn(ex.getMessage());
        }

        LOGGER.info( "Mail Session has been created successfully..");

        // Step3
        LOGGER.info("3rd ===> Get Session and Send mail");
        Transport transport = null;
        try {

            transport = getMailSession.getTransport("smtp");

            // Enter your correct gmail UserID and Password
            // if you have 2FA enabled then provide App Specific Password
            transport.connect("smtp.gmail.com", Settings.ADMIN_EMAIL, Settings.ADMIN_EMAIL_PASSWORD);
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());

            LOGGER.info("Email was sent successfully!!");

        } catch (MessagingException ex) {

            LOGGER.warn(ex.getMessage());

        } finally {

            try {
                assert transport != null;
                transport.close();

            } catch (MessagingException ex) {
                LOGGER.warn(ex.getMessage());
            }
        }
    }
}