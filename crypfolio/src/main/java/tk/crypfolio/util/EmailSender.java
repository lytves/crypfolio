package tk.crypfolio.util;

import tk.crypfolio.common.Settings;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public abstract class EmailSender {

    private static final Logger logger = Logger.getLogger(EmailSender.class.getName());

    /*
     * Outgoing Mail (SMTP) Server
     * requires TLS or SSL: smtp.gmail.com (use authentication)
     * */

    public static void sendConfirmationEmail(String emailRecipient, String verificationCode) {

        String subject = "CrypFolio: confirm your email";

        String text = "<p>Thanks for registering an account on CrypFolio!</p>" +

                "<p>We need to confirm that you're a real human.</p>" +

                "<p>Follow the link to verify your email:</p>" +

                "<p><a href=\"https://crypfolio.tk/verify-email/confirm-link?code=" + verificationCode + "\">" +

                "https://crypfolio.tk/verify-email/confirm-link?code=" + verificationCode + "</a></p>" +

                "<p></p><p>-----------------</p>" +

                "<p>If you have been already confirmed your email, just ignore this email.</p>" +

                "<p>No one is able to hack your CrypFolio account, </p>" +

                "<p>unless they have also compromised your email address.</p>" +

                "<p></p><p></p>" +

                "<p>For security reasons, this link will expire in 30 minutes.</p>";

        logger.log(Level.INFO, "Start sending sendConfirmationEmail()..");

        sendEmail(emailRecipient, subject, text);
    }

    public static void sendResetPasswordEmail(String emailRecipient, String resetCode) {

        String subject = "CrypFolio: reset password";

        String text = "<p>Follow this link and set your new CrypFolio account password:</p>" +

                "<p><a href=\"https://crypfolio.tk/reset-password/reset-link?code=" + resetCode + "\">" +

                "https://crypfolio.tk/reset-password/reset-link?code=" + resetCode + "</a></p>" +

                "<p></p><p>-----------------</p>" +

                "<p>If you did not request to reset your password, just ignore this email.</p>" +

                "<p>No one is able to reset your CrypFolio password without your permission, </p>" +

                "<p>unless they have also compromised your email address.</p>" +

                "<p></p><p></p>" +

                "<p>For security reasons, this link will expire in 30 minutes.</p>";

        logger.log(Level.INFO, "Start sending sendResetPasswordEmail()..");

        sendEmail(emailRecipient, subject, text);
    }

    @Asynchronous
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
            transport.connect("smtp.gmail.com", Settings.ADMIN_EMAIL, Settings.ADMIN_EMAIL_PASSWORD);
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